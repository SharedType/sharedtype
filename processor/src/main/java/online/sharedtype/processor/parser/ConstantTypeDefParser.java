package online.sharedtype.processor.parser;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.DependingKind;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.parser.type.TypeContext;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
final class ConstantTypeDefParser implements TypeDefParser {
    private static final Set<String> SUPPORTED_ELEMENT_KIND = new HashSet<String>(4) {{
        add(ElementKind.CLASS.name());
        add(ElementKind.INTERFACE.name());
        add("RECORD");
        add(ElementKind.ENUM.name());
    }};
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (!SUPPORTED_ELEMENT_KIND.contains(typeElement.getKind().name())) {
            return Collections.emptyList();
        }

        String qualifiedName = typeElement.getQualifiedName().toString();
        List<TypeDef> cachedDefs = ctx.getTypeStore().getTypeDefs(qualifiedName);
        if (cachedDefs == null || cachedDefs.isEmpty()) {
            throw new SharedTypeInternalError("No main type def found for: " + qualifiedName);
        }
        if (shouldSkip(cachedDefs.get(0))) {
            return Collections.emptyList();
        }

        Config config = ctx.getTypeStore().getConfig(qualifiedName);
        if (config == null) {
            config = new Config(typeElement);
        }
        if (!config.includes(SharedType.ComponentType.CONSTANTS)) {
            return Collections.emptyList();
        }
        ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder()
            .qualifiedName(qualifiedName)
            .simpleName(config.getSimpleName())
            .build();

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (ctx.isIgnored(enclosedElement)) {
                continue;
            }

            if (enclosedElement.getKind() == ElementKind.FIELD && enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                ConstantField constantField = new ConstantField(
                    enclosedElement.getSimpleName().toString(),
                    typeInfoParser.parse(enclosedElement.asType(), TypeContext.builder().typeDef(constantNamespaceDef).dependingKind(DependingKind.COMPONENTS).build()),
                    parseConstantValue(enclosedElement, typeElement)
                );
                constantNamespaceDef.components().add(constantField);
            }
        }
        if (constantNamespaceDef.components().isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(constantNamespaceDef);
    }

    private static boolean shouldSkip(TypeDef mainTypeDef) {
        if (mainTypeDef instanceof ClassDef) {
            ClassDef classDef = (ClassDef) mainTypeDef;
            return classDef.isMapType() || classDef.isArrayType();
        }
        return false;
    }

    private Object parseConstantValue(Element fieldElement, TypeElement ctxTypeElement) {
        VariableTree tree = (VariableTree) ctx.getTrees().getTree(fieldElement);
        if (tree == null) {
            throw new SharedTypeInternalError(String.format("Cannot parse constant value for field: %s in %s, tree is null from the field element.",
                fieldElement.getSimpleName(), ctxTypeElement.getQualifiedName()));
        }
        ExpressionTree valueTree = tree.getInitializer();
        if (valueTree instanceof LiteralTree) {
            return ((LiteralTree) valueTree).getValue();
        } else {
            throw new SharedTypeException(String.format("Only literal value is supported for constant field." +
                " Field: %s in %s", fieldElement.getSimpleName(), ctxTypeElement.getQualifiedName()));
        }
    }
}

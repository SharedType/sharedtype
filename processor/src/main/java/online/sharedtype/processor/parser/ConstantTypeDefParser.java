package online.sharedtype.processor.parser;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.Trees;
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

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ConstantTypeDefParser implements TypeDefParser {
    private static final Set<String> SUPPORTED_ELEMENT_KIND = new HashSet<>(4);
    private final static Set<String> TYPE_ELEMENT_KIND = new HashSet<>(4);
    static {
        SUPPORTED_ELEMENT_KIND.add(ElementKind.CLASS.name());
        SUPPORTED_ELEMENT_KIND.add(ElementKind.INTERFACE.name());
        SUPPORTED_ELEMENT_KIND.add("RECORD");
        SUPPORTED_ELEMENT_KIND.add(ElementKind.ENUM.name());

        TYPE_ELEMENT_KIND.add(ElementKind.ENUM.name());
        TYPE_ELEMENT_KIND.add(ElementKind.CLASS.name());
        TYPE_ELEMENT_KIND.add(ElementKind.INTERFACE.name());
        TYPE_ELEMENT_KIND.add("RECORD");
    }

    private final Context ctx;
    private final TypeInfoParser typeInfoParser;
    private final Trees trees;
    private final Elements elements;
    private final Types types;
    ConstantTypeDefParser(Context ctx, TypeInfoParser typeInfoParser) {
        this.ctx = ctx;
        this.typeInfoParser = typeInfoParser;
        this.trees = ctx.getTrees();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        this.types = ctx.getProcessingEnv().getTypeUtils();
    }

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (!SUPPORTED_ELEMENT_KIND.contains(typeElement.getKind().name())) {
            return Collections.emptyList();
        }
        if (ctx.getTrees() == null) {
            ctx.info(typeElement, "Skip parsing constants for type %s, because tree is not available.", typeElement);
            return Collections.emptyList();
        }

        String qualifiedName = typeElement.getQualifiedName().toString();
        List<TypeDef> cachedDefs = ctx.getTypeStore().getTypeDefs(qualifiedName);
        if (cachedDefs == null || cachedDefs.isEmpty()) {
            throw new SharedTypeInternalError("No main type def found for: " + qualifiedName);
        }
        TypeDef mainTypeDef = cachedDefs.get(0);
        if (shouldSkip(mainTypeDef)) {
            return Collections.emptyList();
        }

        Config config = ctx.getTypeStore().getConfig(mainTypeDef);
        if (!config.includes(SharedType.ComponentType.CONSTANTS)) {
            return Collections.emptyList();
        }
        ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder().element(typeElement)
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
            return classDef.isMapType() || !classDef.isAnnotated();
        }
        return false;
    }

    private Object parseConstantValue(Element fieldElement, TypeElement ctxTypeElement) {
        VariableTree tree = (VariableTree) ctx.getTrees().getTree(fieldElement);
        if (tree == null) {
            throw new SharedTypeInternalError(String.format("Cannot parse constant value for field: %s in %s, tree is null from the field element. " +
                    "If the type is from a dependency jar/compiled class file, tree is not available at the time of annotation processing. " +
                    "Check if the type or its custom mapping is correct.",
                fieldElement.getSimpleName(), ctxTypeElement.getQualifiedName()));
        }
        ExpressionTree valueTree = tree.getInitializer();
        if (valueTree instanceof LiteralTree) {
            return ((LiteralTree) valueTree).getValue();
        } else {
            Scope scope = ctx.getTrees().getScope(ctx.getTrees().getPath(fieldElement));
            Element referencedFieldElement = recursivelyResolveReferencedElement(valueTree, scope, ctxTypeElement);
            if (referencedFieldElement != null) {
                return parseConstantValue(referencedFieldElement, ctxTypeElement);
            }

            ctx.error(ctxTypeElement, "Only literal value is supported for constant field." +
                    " Field: %s in %s. Consider use @SharedType.Ignore to ignore this field or exclude constants generation for this type.",
                fieldElement.getSimpleName(), ctxTypeElement.getQualifiedName());
            return null;
        }
    }

    private Element recursivelyResolveReferencedElement(ExpressionTree valueTree, Scope scope, TypeElement enclosingTypeElement) {
        if (valueTree instanceof IdentifierTree) {
            IdentifierTree identifierTree = (IdentifierTree) valueTree;
            Element referencedElement = findReferencedElementInTree(scope, identifierTree.getName().toString());
            if (referencedElement == null) {
                referencedElement = findEnclosedElement(enclosingTypeElement, identifierTree.getName().toString());
            }
            return referencedElement;
        } if (valueTree instanceof MemberSelectTree) {
            MemberSelectTree memberSelectTree = (MemberSelectTree) valueTree;
            ExpressionTree expressionTree = memberSelectTree.getExpression();
            Element selecteeElement = recursivelyResolveReferencedElement(expressionTree, scope, enclosingTypeElement);
            if (!(selecteeElement instanceof TypeElement)) {
                throw new SharedTypeException(String.format(
                    "A selectee element must be typeElement, but found: %s in %s", selecteeElement, enclosingTypeElement));
            }
            return findEnclosedElement((TypeElement) selecteeElement, memberSelectTree.getIdentifier().toString());
        }
        return null;
    }

    private Element findEnclosedElement(TypeElement enclosingTypeElement, String name) {
        for (Element element : enclosingTypeElement.getEnclosedElements()) {
            if (element.getSimpleName().contentEquals(name)) {
//                if (element.getKind() != ElementKind.FIELD) {
//                    ctx.error(enclosingTypeElement,
//                        "Constant field '%s' is referencing a non-field element: %s in %s, which is not supported.",
//                        name, element.getSimpleName(), enclosingTypeElement.getQualifiedName());
//                }
                return element;
            }
        }
        throw new SharedTypeException(String.format("Cannot find referenced local element: %s in %s", enclosingTypeElement.getQualifiedName(), name));
    }

    /** Try to find referenced type element in the file scope, including: local references and imported references */
    @Nullable
    private Element findReferencedElementInTree(Scope scope, String name) {
        Scope curScope = scope;
        while (curScope != null) {
            for (Element element : curScope.getLocalElements()) {
                if (element.getSimpleName().contentEquals(name) && TYPE_ELEMENT_KIND.contains(element.getKind().name())) {
                    return element;
                }
            }
            curScope = curScope.getEnclosingScope();
        }
        return null;
    }


}

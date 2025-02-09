package online.sharedtype.processor.parser;

import com.sun.source.tree.Tree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.DependingKind;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.parser.type.TypeContext;
import online.sharedtype.processor.parser.type.TypeInfoParser;

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
            if (enclosedElement.getAnnotation(SharedType.Ignore.class) != null) {
                continue;
            }
            if (enclosedElement.getKind() == ElementKind.FIELD && enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                ConstantField constantField = new ConstantField(
                    enclosedElement.getSimpleName().toString(),
                    typeInfoParser.parse(enclosedElement.asType(), TypeContext.builder().typeDef(constantNamespaceDef).dependingKind(DependingKind.COMPONENTS).build()),
                    parseConstantValue(enclosedElement)
                );
                constantNamespaceDef.components().add(constantField);
            }
        }

        return Collections.singletonList(constantNamespaceDef);
    }

    private Object parseConstantValue(Element fieldElement) {
        Tree tree = ctx.getTrees().getTree(fieldElement);
        return null;
    }
}

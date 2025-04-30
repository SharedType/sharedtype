package online.sharedtype.processor.parser;

import com.sun.source.tree.Tree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.parser.value.ValueResolver;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Literal values are parsed via {@link Tree} API. It has limitations, see the documentation for more details.
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class EnumTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;
    private final ValueResolver valueResolver;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (typeElement.getKind() != ElementKind.ENUM) {
            return Collections.emptyList();
        }
        if (ctx.getTrees() == null) {
            ctx.info(typeElement, "Skip parsing enum %s, because tree is not available.", typeElement);
            return Collections.emptyList();
        }

        Config config = new Config(typeElement, ctx);
        ctx.getTypeStore().saveConfig(config);

        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<VariableElement> enumConstantElems = new ArrayList<>(enclosedElements.size());

        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.ENUM_CONSTANT) {
                enumConstantElems.add((VariableElement) enclosedElement);
            }
        }

        EnumDef enumDef = EnumDef.builder().element(typeElement)
            .qualifiedName(config.getQualifiedName())
            .simpleName(config.getSimpleName())
            .annotated(config.isAnnotated())
            .build();
        enumDef.components().addAll(parseEnumConstants(typeElement, enumConstantElems));
        TypeInfo typeInfo = typeInfoParser.parse(typeElement.asType(), typeElement);
        enumDef.linkTypeInfo((ConcreteTypeInfo) typeInfo);
        return Collections.singletonList(enumDef);
    }

    private List<EnumValueInfo> parseEnumConstants(TypeElement enumTypeElement, List<VariableElement> enumConstants) {
        List<EnumValueInfo> res = new ArrayList<>(enumConstants.size());
        for (VariableElement enumConstant : enumConstants) {
            String name = enumConstant.getSimpleName().toString();
            ValueHolder value = valueResolver.resolve(enumConstant, enumTypeElement);
            if (value instanceof EnumConstantValue) {
                res.add(new EnumValueInfo(name, (EnumConstantValue) value));
            } else {
                ctx.warn(enumConstant, "Cannot resolve value for enum constant %s, value: '%s'", name, value);
            }
        }
        return res;
    }
}

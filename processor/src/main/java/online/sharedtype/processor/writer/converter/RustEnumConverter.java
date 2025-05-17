package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
final class RustEnumConverter extends AbstractEnumConverter {
    private final TypeExpressionConverter typeExpressionConverter;
    private final RustMacroTraitsGenerator rustMacroTraitsGenerator;

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        String valueType = getValueTypeExpr(enumDef);
        // value can be literal or enum
        EnumExpr value = new EnumExpr(
            enumDef.simpleName(),
            extractEnumValues(enumDef.components()),
            rustMacroTraitsGenerator.generate(enumDef),
            valueType != null,
            valueType
        );
        return Tuple.of(Template.TEMPLATE_RUST_ENUM, value);
    }

    @Nullable
    private String getValueTypeExpr(EnumDef enumDef) {
        TypeInfo componentValueType = enumDef.getComponentValueType();
        TypeInfo enumTypeInfo = enumDef.typeInfoSet().iterator().next();
        if (enumTypeInfo.equals(componentValueType)) {
            return null;
        }
        return typeExpressionConverter.toTypeExpr(componentValueType, enumDef);
    }

    private List<EnumerationExpr> extractEnumValues(List<EnumValueInfo> components) {
        List<EnumerationExpr> exprs = new ArrayList<>(components.size());

        for (EnumValueInfo component : components) {
            boolean isEnum = component.value().getValueType().getKind() == ConcreteTypeInfo.Kind.ENUM;
            exprs.add(new EnumerationExpr(
                component.name(),
                isEnum ? rustEnumSelectExpr(component.value()) : component.value().literalValue()
            ));
        }
        return exprs;
    }

    private static String rustEnumSelectExpr(EnumConstantValue enumConstantValue) {
        String enumName = enumConstantValue.getValueType().simpleName();
        String variantName = Objects.toString(enumConstantValue.getValue());
        return String.format("%s::%s", enumName, variantName);
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr {
        final String name;
        final List<EnumerationExpr> enumerations;
        final Set<String> macroTraits;
        final boolean hasValue;
        final String valueType;

        String macroTraitsExpr() {
            return ConversionUtils.buildRustMacroTraitsExpr(macroTraits);
        }
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumerationExpr {
        final String name;
        @Nullable
        final String value;
    }
}

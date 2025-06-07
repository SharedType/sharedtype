package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.component.ComponentInfo;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import online.sharedtype.processor.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
final class RustEnumConverter extends AbstractEnumConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;
    private final RustMacroTraitsGenerator rustMacroTraitsGenerator;

    @Override
    public Tuple<Template, AbstractTypeExpr> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        String valueType = getValueTypeExpr(enumDef);
        boolean hasValue = valueType != null;
        // value can be literal or enum
        EnumExpr value = new EnumExpr(
            enumDef.simpleName(),
            extractEnumValues(enumDef.components()),
            rustMacroTraitsGenerator.generate(enumDef),
            hasValue,
            valueType,
            hasValue && ctx.getProps().getRust().hasEnumValueTypeAlias() ? enumDef.valueTypeAlias() : null
        );
        return Tuple.of(Template.TEMPLATE_RUST_ENUM, value);
    }

    @Nullable
    private String getValueTypeExpr(EnumDef enumDef) {
        if (enumDef.hasComponentValueType()) {
            return typeExpressionConverter.toTypeExpr(enumDef.getComponentValueType(), enumDef);
        }
        return null;
    }

    private List<EnumerationExpr> extractEnumValues(List<EnumValueInfo> components) {
        List<EnumerationExpr> exprs = new ArrayList<>(components.size());

        for (EnumValueInfo component : components) {
            boolean isEnum = component.value().getValueType().getKind() == ConcreteTypeInfo.Kind.ENUM;
            exprs.add(new EnumerationExpr(
                component,
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
    static final class EnumExpr extends AbstractTypeExpr {
        final String name;
        final List<EnumerationExpr> enumerations;
        final Set<String> macroTraits;
        final boolean hasValue;
        @Nullable
        final String valueType;
        @Nullable
        final String valueTypeAlias;

        String macroTraitsExpr() {
            return ConversionUtils.buildRustMacroTraitsExpr(macroTraits);
        }
    }

    @SuppressWarnings("unused")
    static final class EnumerationExpr extends AbstractFieldExpr {
        @Nullable
        final String value;
        EnumerationExpr(ComponentInfo componentInfo, @Nullable String value) {
            super(componentInfo, SharedType.TargetType.RUST);
            this.value = value;
        }
    }
}

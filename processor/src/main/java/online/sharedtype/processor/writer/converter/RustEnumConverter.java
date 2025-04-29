package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
final class RustEnumConverter implements TemplateDataConverter {
    private final TypeExpressionConverter typeExpressionConverter;
    private final RustMacroTraitsGenerator rustMacroTraitsGenerator;

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof EnumDef && !((EnumDef) typeDef).components().isEmpty();
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        String valueType = getValueTypeExpr(enumDef);
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
        EnumValueInfo component = enumDef.components().get(0);
        TypeInfo enumTypeInfo = enumDef.typeInfoSet().iterator().next();
        if (enumTypeInfo.equals(component.type())) {
            return null;
        }
        return typeExpressionConverter.toTypeExpr(component.type(), enumDef);
    }

    private List<EnumerationExpr> extractEnumValues(List<EnumValueInfo> components) {
        List<EnumerationExpr> exprs = new ArrayList<>(components.size());

        for (EnumValueInfo component : components) {
            exprs.add(new EnumerationExpr(
                component.name(),
                component.value().literalValue()
            ));
        }
        return exprs;
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr {
        final String name;
        final List<EnumerationExpr> enumerations;
        final Set<String> macroTraits;
        final boolean hasLiteralValue;
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

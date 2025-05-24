package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.component.ComponentInfo;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import online.sharedtype.processor.support.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static online.sharedtype.processor.context.Props.Go.EnumFormat.CONST;
import static online.sharedtype.processor.writer.render.Template.TEMPLATE_GO_CONST_ENUM;
import static online.sharedtype.processor.writer.render.Template.TEMPLATE_GO_STRUCT_ENUM;

@RequiredArgsConstructor
final class GoEnumConverter extends AbstractEnumConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;
    @Override
    public Tuple<Template, AbstractTypeExpr> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        String valueType = getValueTypeExpr(enumDef);

        EnumExpr value = new EnumExpr(
            enumDef.simpleName(),
            enumDef.components().stream().map(comp -> buildEnumExpr(comp, enumDef.simpleName())).collect(Collectors.toList()),
            valueType
        );

        Config config = ctx.getTypeStore().getConfig(typeDef);
        return Tuple.of(config.getGoEnumFormat() == CONST ? TEMPLATE_GO_CONST_ENUM : TEMPLATE_GO_STRUCT_ENUM, value);
    }

    private static EnumerationExpr buildEnumExpr(EnumValueInfo component, String valueTypeExpr) {
        return new EnumerationExpr(
            component,
            valueTypeExpr,
            component.value().literalValue()
        );
    }

    private String getValueTypeExpr(EnumDef enumDef) {
        EnumValueInfo component = enumDef.components().get(0);
        TypeInfo enumTypeInfo = enumDef.typeInfoSet().iterator().next();
        if (enumTypeInfo.equals(component.value().getValueType())) {
            return "string";
        }
        return typeExpressionConverter.toTypeExpr(component.value().getValueType(), enumDef);
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr extends AbstractTypeExpr {
        final String name;
        final List<EnumerationExpr> enumerations;
        final String valueType;
    }

    @SuppressWarnings("unused")
    static final class EnumerationExpr extends AbstractFieldExpr {
        final String enumName;
        @Nullable
        final String value;
        EnumerationExpr(ComponentInfo componentInfo, String enumName, @Nullable String value) {
            super(componentInfo, SharedType.TargetType.GO);
            this.enumName = enumName;
            this.value = value;
        }
    }
}

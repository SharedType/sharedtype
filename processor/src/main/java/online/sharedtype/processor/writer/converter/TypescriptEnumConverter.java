package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.domain.component.ComponentInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class TypescriptEnumConverter extends AbstractEnumConverter {
    private final Context ctx;

    @Override
    public Tuple<Template, AbstractTypeExpr> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        Config config = ctx.getTypeStore().getConfig(typeDef);
        if (config.getTypescriptEnumFormat() == Props.Typescript.EnumFormat.UNION) {
            List<String> values = new ArrayList<>(enumDef.components().size());
            for (EnumValueInfo component : enumDef.components()) {
                values.add(component.value().literalValue());
            }
            return Tuple.of(Template.TEMPLATE_TYPESCRIPT_UNION_TYPE_ENUM, new EnumUnionExpr(enumDef.simpleName(), values));
        } else {
            EnumExpr value = new EnumExpr(
                enumDef.simpleName(),
                config.getTypescriptEnumFormat() == Props.Typescript.EnumFormat.CONST_ENUM,
                enumDef.components().stream().map(component -> new EnumValueExpr(
                    component,
                    component.value().literalValue()
                )).collect(Collectors.toList())
            );
            return Tuple.of(Template.TEMPLATE_TYPESCRIPT_ENUM, value);
        }
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr extends AbstractTypeExpr {
        final String name;
        final boolean isConst;
        final List<EnumValueExpr> values;
    }

    @SuppressWarnings("unused")
    static final class EnumValueExpr extends AbstractFieldExpr {
        final String value;
        EnumValueExpr(ComponentInfo componentInfo, String value) {
            super(componentInfo, SharedType.TargetType.TYPESCRIPT);
            this.value = value;
        }
    }

    @RequiredArgsConstructor
    static final class EnumUnionExpr extends AbstractTypeExpr {
        final String name;
        final List<String> values;

        @SuppressWarnings("unused")
        String valuesExpr() {
            return String.join(" | ", values);
        }
    }
}

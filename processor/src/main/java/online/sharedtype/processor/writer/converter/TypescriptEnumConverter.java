package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class TypescriptEnumConverter implements TemplateDataConverter {
    private final Context ctx;
    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof EnumDef && !((EnumDef) typeDef).components().isEmpty();
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        Config config = ctx.getTypeStore().getConfig(typeDef);
        if (config.getTypescriptEnumFormat() == Props.Typescript.EnumFormat.UNION) {
            List<String> values = new ArrayList<>(enumDef.components().size());
            for (EnumValueInfo component : enumDef.components()) {
                values.add(ConversionUtils.literalValue(component.value()));
            }
            return Tuple.of(Template.TEMPLATE_TYPESCRIPT_UNION_TYPE_ENUM, new EnumUnionExpr(enumDef.simpleName(), values));
        } else {
            EnumExpr value = new EnumExpr(
                enumDef.simpleName(),
                config.getTypescriptEnumFormat() == Props.Typescript.EnumFormat.CONST_ENUM,
                enumDef.components().stream().map(component -> new EnumValueExpr(
                    component.name(),
                    ConversionUtils.literalValue(component.value())
                )).collect(Collectors.toList())
            );
            return Tuple.of(Template.TEMPLATE_TYPESCRIPT_ENUM, value);
        }
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr {
        final String name;
        final boolean isConst;
        final List<EnumValueExpr> values;
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumValueExpr {
        final String name;
        final String value;
    }

    @RequiredArgsConstructor
    static final class EnumUnionExpr {
        final String name;
        final List<String> values;

        @SuppressWarnings("unused")
        String valuesExpr() {
            return String.join(" | ", values);
        }
    }
}

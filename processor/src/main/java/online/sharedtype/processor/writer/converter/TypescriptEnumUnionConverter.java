package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.writer.render.Template;
import online.sharedtype.support.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

final class TypescriptEnumUnionConverter implements TemplateDataConverter {
    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        if (typeDef instanceof EnumDef) {
            EnumDef enumDef = (EnumDef) typeDef;
            List<String> values = new ArrayList<>(enumDef.components().size());
            for (EnumValueInfo component : enumDef.components()) {
                if (LiteralUtils.shouldQuote(component.value())) {
                    values.add(String.format("\"%s\"", component.value())); // TODO: options single or double quotes?
                } else {
                    values.add(String.valueOf(component.value()));
                }
            }
            return Tuple.of(Template.TEMPLATE_TYPESCRIPT_ENUM_UNION, new EnumUnionExpr(enumDef.simpleName(), values));
        }
        return null;
    }
}

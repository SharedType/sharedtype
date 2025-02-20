package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;

final class TypescriptEnumUnionConverter implements TemplateDataConverter {
    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof EnumDef && !((EnumDef) typeDef).components().isEmpty();
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;
        List<String> values = new ArrayList<>(enumDef.components().size());
        for (EnumValueInfo component : enumDef.components()) {
            values.add(LiteralUtils.literalValue(component.value()));
        }
        return Tuple.of(Template.TEMPLATE_TYPESCRIPT_ENUM_UNION, new EnumUnionExpr(enumDef.simpleName(), values));
    }
}

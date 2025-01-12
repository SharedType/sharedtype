package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;

final class RustEnumConverter implements TemplateDataConverter {
    @Override
    public boolean supports(TypeDef typeDef) {
        return typeDef instanceof EnumDef;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        EnumExpr value = new EnumExpr(
            enumDef.simpleName(),
            extractEnumValues(enumDef.components())
        );
        return Tuple.of(Template.TEMPLATE_RUST_ENUM, value);
    }

    private static List<EnumerationExpr> extractEnumValues(List<EnumValueInfo> components) {
        List<EnumerationExpr> exprs = new ArrayList<>(components.size());
        for (EnumValueInfo component : components) {
            exprs.add(new EnumerationExpr(component.name()));
        }
        return exprs;
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr {
        final String name;
        final List<EnumerationExpr> enumerations;
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumerationExpr {
        final String name;
    }
}

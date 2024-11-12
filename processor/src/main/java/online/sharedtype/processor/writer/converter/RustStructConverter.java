package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;

final class RustStructConverter implements TemplateDataConverter {
    private final TypeExpressionConverter typeExpressionConverter;

    RustStructConverter(TypeExpressionConverter typeExpressionConverter) {
        this.typeExpressionConverter = typeExpressionConverter;
    }

    @Override
    public boolean supports(TypeDef typeDef) {
        return typeDef instanceof ClassDef;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        List<PropertyExpr> properties = new ArrayList<>();
        for (FieldComponentInfo component : classDef.components()) {
            properties.add(new PropertyExpr(
                component.name(),
                typeExpressionConverter.toTypeExpr(component.type()),
                component.optional()
            ));
        }
        // TODO: supertypes

        StructExpr value = new StructExpr(
            classDef.simpleName(),
            properties
        );
        return Tuple.of(Template.TEMPLATE_RUST_STRUCT, value);
    }

    @RequiredArgsConstructor
    static final class StructExpr {
        final String name;
        final List<PropertyExpr> properties;
    }

    @RequiredArgsConstructor
    static final class PropertyExpr {
        final String name;
        final String type;
        final boolean optional;
    }
}

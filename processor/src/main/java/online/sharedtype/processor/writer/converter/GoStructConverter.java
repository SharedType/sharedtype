package online.sharedtype.processor.writer.converter;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import online.sharedtype.processor.domain.component.FieldComponentInfo;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class GoStructConverter extends AbstractStructConverter {
    private final TypeExpressionConverter typeExpressionConverter;

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        StructExpr value = new StructExpr(
            classDef.simpleName(),
            classDef.typeVariables().stream().map(typeInfo -> typeExpressionConverter.toTypeExpr(typeInfo, typeDef)).collect(Collectors.toList()),
            classDef.directSupertypes().stream().map(typeInfo1 -> typeExpressionConverter.toTypeExpr(typeInfo1, typeDef)).collect(Collectors.toList()),
            gatherProperties(classDef)
        );
        return Tuple.of(Template.TEMPLATE_GO_STRUCT, value);
    }

    private List<PropertyExpr> gatherProperties(ClassDef classDef) {
        List<PropertyExpr> properties = new ArrayList<>();
        for (FieldComponentInfo component : classDef.components()) {
            properties.add(toPropertyExpr(component, classDef));
        }
        return properties;
    }

    private PropertyExpr toPropertyExpr(FieldComponentInfo field, TypeDef contextTypeDef) {
        return new PropertyExpr(
            ConversionUtils.capitalize(field.name()),
            typeExpressionConverter.toTypeExpr(field.type(), contextTypeDef),
            ConversionUtils.isOfCyclicReferencedType(field)
        );
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class StructExpr {
        final String name;
        final List<String> typeParameters;
        final List<String> supertypes;
        final List<PropertyExpr> properties;

        String typeParametersExpr() {
            if (typeParameters.isEmpty()) {
                return null;
            }
            return String.format("[%s any]", String.join(", ", typeParameters));
        }
    }

    @ToString
    @SuppressWarnings("unused")
    @EqualsAndHashCode(of = "name")
    @RequiredArgsConstructor
    static final class PropertyExpr {
        final String name;
        final String type;
        final boolean cyclicReferenced;

        String typeExpr() {
            if (cyclicReferenced) {
                return String.format("*%s", type);
            }
            return type;
        }
    }
}

package online.sharedtype.processor.writer.converter;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

final class RustStructConverter extends AbstractRustConverter {
    private final TypeExpressionConverter typeExpressionConverter;

    RustStructConverter(Context ctx, TypeExpressionConverter typeExpressionConverter) {
        super(ctx);
        this.typeExpressionConverter = typeExpressionConverter;
    }

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        if (!(typeDef instanceof ClassDef)) {
            return false;
        }
        ClassDef classDef = (ClassDef) typeDef;
        if (classDef.isMapType()) {
            return false;
        }
        if (classDef.isAnnotated()) {
            return !classDef.components().isEmpty();
        }
        return classDef.isReferencedByAnnotated();
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        StructExpr value = new StructExpr(
            classDef.simpleName(),
            classDef.typeVariables().stream().map(typeInfo -> typeExpressionConverter.toTypeExpr(typeInfo, typeDef)).collect(Collectors.toList()),
            gatherProperties(classDef),
            macroTraits(classDef)
        );
        return Tuple.of(Template.TEMPLATE_RUST_STRUCT, value);
    }

    private List<PropertyExpr> gatherProperties(ClassDef classDef) {
        List<PropertyExpr> properties = new ArrayList<>(); // TODO: init cap
        Set<String> propertyNames = new HashSet<>();
        for (FieldComponentInfo component : classDef.components()) {
            properties.add(toPropertyExpr(component, classDef));
            propertyNames.add(component.name());
        }

        Queue<TypeInfo> superTypes = new ArrayDeque<>(classDef.directSupertypes());
        while (!superTypes.isEmpty()) {
            TypeInfo supertype = superTypes.poll();
            if (supertype instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo superConcreteTypeInfo = (ConcreteTypeInfo) supertype;
                ClassDef superTypeDef = (ClassDef) superConcreteTypeInfo.typeDef(); // supertype must be ClassDef
                if (superTypeDef != null) {
                    superTypeDef = superTypeDef.reify(superConcreteTypeInfo.typeArgs());
                    for (FieldComponentInfo component : superTypeDef.components()) {
                        if (!propertyNames.contains(component.name())) {
                            properties.add(toPropertyExpr(component, superTypeDef));
                            propertyNames.add(component.name());
                        }
                    }
                    superTypes.addAll(superTypeDef.directSupertypes());
                }
            }
        }
        return properties;
    }

    private PropertyExpr toPropertyExpr(FieldComponentInfo field, TypeDef contextTypeDef) {
        return new PropertyExpr(
            ctx.getProps().getRust().isConvertToSnakeCase() ? ConversionUtils.toSnakeCase(field.name()) : field.name(),
            typeExpressionConverter.toTypeExpr(field.type(), contextTypeDef),
            ConversionUtils.isOptionalField(field)
        );
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class StructExpr {
        final String name;
        final List<String> typeParameters;
        final List<PropertyExpr> properties;
        final Set<String> macroTraits;

        String typeParametersExpr() {
            if (typeParameters.isEmpty()) {
                return null;
            }
            return String.format("<%s>", String.join(", ", typeParameters));
        }

        String macroTraitsExpr() {
            return buildMacroTraitsExpr(macroTraits);
        }
    }

    @ToString
    @SuppressWarnings("unused")
    @EqualsAndHashCode(of = "name")
    @RequiredArgsConstructor
    static final class PropertyExpr {
        final String name;
        final String type;
        final boolean optional;

        String typeExpr() {
            return optional ? String.format("Option<%s>", type) : type;
        }
    }
}

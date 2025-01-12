package online.sharedtype.processor.writer.converter;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
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
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class RustStructConverter implements TemplateDataConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        if (!(typeDef instanceof ClassDef)) {
            return false;
        }
        ClassDef classDef = (ClassDef) typeDef;
        if (classDef.isAnnotated()) {
            return true;
        }

        Set<ClassDef> visitedClassDef = new HashSet<>();
        visitedClassDef.add(classDef);
        Deque<ClassDef> referencingClassDefs = new ArrayDeque<>();
        referencingClassDefs.push(classDef);
        while (!referencingClassDefs.isEmpty()) {
            ClassDef cur = referencingClassDefs.pop();
            List<String> referencingTypeQualifiedNames = cur.typeInfoSet().stream()
                .flatMap(ts -> ts.referencingTypeQualifiedNames().stream()).collect(Collectors.toList());
            for (String referencingTypeQualifiedName : referencingTypeQualifiedNames) {
                TypeDef dependingTypeDef = ctx.getTypeStore().getTypeDef(referencingTypeQualifiedName);
                if (dependingTypeDef instanceof ClassDef) {
                    if (((ClassDef) dependingTypeDef).isAnnotated()) {
                        return true;
                    }
                    if (!visitedClassDef.contains(dependingTypeDef)) {
                        referencingClassDefs.push((ClassDef) dependingTypeDef);
                        visitedClassDef.add(classDef);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        StructExpr value = new StructExpr(
            classDef.simpleName(),
            classDef.typeVariables().stream().map(typeExpressionConverter::toTypeExpr).collect(Collectors.toList()),
            gatherProperties(classDef)
        );
        return Tuple.of(Template.TEMPLATE_RUST_STRUCT, value);
    }

    private PropertyExpr toPropertyExpr(FieldComponentInfo field) {
        return new PropertyExpr(
            field.name().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase(), // TODO: optimize
            typeExpressionConverter.toTypeExpr(field.type()),
            field.optional()
        );
    }

    private List<PropertyExpr> gatherProperties(ClassDef classDef) {
        List<PropertyExpr> properties = new ArrayList<>(); // TODO: init cap
        Set<String> propertyNames = new HashSet<>();
        for (FieldComponentInfo component : classDef.components()) {
            properties.add(toPropertyExpr(component));
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
                            properties.add(toPropertyExpr(component));
                            propertyNames.add(component.name());
                        }
                    }
                    superTypes.addAll(superTypeDef.directSupertypes());
                }
            }
        }
        return properties;
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class StructExpr {
        final String name;
        final List<String> typeParameters;
        final List<PropertyExpr> properties;

        String typeParametersExpr() {
            if (typeParameters.isEmpty()) {
                return null;
            }
            return String.format("<%s>", String.join(", ", typeParameters));
        }
    }

    @SuppressWarnings("unused")
    @EqualsAndHashCode(of = "name")
    @RequiredArgsConstructor
    static final class PropertyExpr {
        final String name;
        final String type;
        final boolean optional;
    }
}

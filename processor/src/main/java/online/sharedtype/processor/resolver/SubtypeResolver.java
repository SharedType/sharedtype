package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;

import java.util.List;

/**
 * Resolve subtype relationships.
 */
final class SubtypeResolver implements TypeResolver {
    @Override
    public List<TypeDef> resolve(List<TypeDef> typeDefs) {
        for (TypeDef typeDef : typeDefs) {
            traverseDirectSupertypes(typeDef);
        }
        return typeDefs;
    }

    private static void traverseDirectSupertypes(TypeDef typeDef) {
        for (TypeInfo typeInfo : typeDef.directSupertypes()) {
            if (typeInfo instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
                TypeDef supertypeDef = concreteTypeInfo.typeDef();
                if (supertypeDef instanceof ClassDef) {
                    ClassDef supertypeClassDef = (ClassDef) supertypeDef;
                    supertypeClassDef.addSubtype(typeDef);
                    supertypeClassDef.setDepended(true);
                }
            }
        }
    }
}

package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;

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

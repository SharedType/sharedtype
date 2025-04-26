package online.sharedtype.processor.domain.type;

import online.sharedtype.processor.domain.def.TypeDef;

import java.util.HashSet;
import java.util.Set;

public abstract class ReferableTypeInfo implements TypeInfo {
    private static final long serialVersionUID = -8637192825773596439L;
    /**
     * Qualified names of types from where this typeInfo is strongly referenced, i.e. as a component type.
     */
    private final Set<TypeDef> referencingTypes = new HashSet<>();

    @Override
    public final void addReferencingType(TypeDef typeDef) {
        referencingTypes.add(typeDef);
    }

    public final Set<TypeDef> referencingTypes() {
        return referencingTypes;
    }
}

package online.sharedtype.processor.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder(toBuilder = true)
@Setter
@Getter
public abstract class ConcreteTypeDef implements TypeDef {
    private static final long serialVersionUID = 2346502341189835693L;
    /** Whether this type is explicitly annotated with {@link online.sharedtype.SharedType} */
    private final boolean annotated;
    /** Directly or indirectly referenced by a type that is annotated. */
    private boolean referencedByAnnotated;

    /** Referenced or as a supertype of another type. */
    private boolean depended;

    private boolean cyclicReferenced;

    public abstract Set<ConcreteTypeInfo> typeInfoSet();

    /**
     * Register a counterpart typeInfo.
     */
    public abstract void linkTypeInfo(ConcreteTypeInfo typeInfo);
}

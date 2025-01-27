package online.sharedtype.processor.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Setter
@Getter
public abstract class AbstractTypeDef implements TypeDef {
    private static final long serialVersionUID = 5283247494912582726L;
    /** Whether this type is explicitly annotated with {@link online.sharedtype.SharedType} */
    private boolean annotated;
    /** Directly or indirectly referenced by a type that is annotated. */
    private boolean referencedByAnnotated;

    private boolean cyclicReferenced;
}

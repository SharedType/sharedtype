package online.sharedtype.processor.domain.def;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;

import javax.lang.model.element.Element;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@Setter
@Getter
public abstract class ConcreteTypeDef implements TypeDef {
    private static final long serialVersionUID = 2346502341189835693L;
    @Getter
    private final transient Element element;
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
     * <b>Note:</b> This method should not be called from outside, please use {@link ConcreteTypeInfo#shallowResolved()}
     */
    public abstract void linkTypeInfo(ConcreteTypeInfo typeInfo);
}

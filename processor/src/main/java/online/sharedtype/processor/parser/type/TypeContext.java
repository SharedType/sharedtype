package online.sharedtype.processor.parser.type;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.processor.domain.DependingKind;

@EqualsAndHashCode
@Builder
@Getter
public final class TypeContext {
    /** The name of the context type definition. */
    private final String qualifiedName;
    private final DependingKind dependingKind;
}

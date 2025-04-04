package online.sharedtype.processor.parser.type;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import online.sharedtype.processor.domain.DependingKind;
import online.sharedtype.processor.domain.TypeDef;

@EqualsAndHashCode
@Builder
@Getter
@ToString
public final class TypeContext {
    /** The context type definition. */
    private final TypeDef typeDef;
    private final DependingKind dependingKind;
}

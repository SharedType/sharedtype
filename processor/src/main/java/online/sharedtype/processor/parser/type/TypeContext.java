package online.sharedtype.processor.parser.type;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public final class TypeContext {
    /** The name of the context type definition. */
    private final String qualifiedName;
    private final boolean isTypeArgument;
}

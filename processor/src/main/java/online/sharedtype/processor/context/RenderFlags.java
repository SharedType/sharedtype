package online.sharedtype.processor.context;

import lombok.Getter;
import lombok.Setter;

/**
 * Flags that can only be marked during processing runtime.
 * Anything that can be configured beforehand should be in {@link Props}.
 */
@Getter
@Setter
public final class RenderFlags {
    private boolean useRustAny = false;
    private boolean useRustMap = false;
}

package online.sharedtype.processor.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import online.sharedtype.processor.support.annotation.Nullable;
import javax.lang.model.element.VariableElement;


@RequiredArgsConstructor
@Getter
public final class EnumCtorIndex {
    public static final EnumCtorIndex NONE = new EnumCtorIndex(-1, null);
    /**
     * The index of enum constant constructor argument, or -1 if no constructor argument.
     */
    private final int idx;
    private final VariableElement field;
}

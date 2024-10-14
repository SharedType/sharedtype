package org.sharedtype.processor.parser.field;

import org.sharedtype.annotation.EmitType;
import org.sharedtype.processor.domain.TypeInfo;

import javax.lang.model.element.VariableElement;

public interface VariableElementParser {
    /**
     * If a dependent type is not explicitly registered by {@link EmitType},
     * it may not have been resolved in the context by the time of the mapper call.
     * In such case, the caller would resolve the type in another iteration and map the type again.
     * <p>
     * An unresolved type is a type whose TypeElement hasn't been processed.
     * So there's no information, e.g. name, saved in global context yet.
     * </p>
     */
    TypeInfo parse(VariableElement element);

}

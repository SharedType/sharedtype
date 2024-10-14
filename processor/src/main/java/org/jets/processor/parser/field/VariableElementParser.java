package org.jets.processor.parser.field;

import org.jets.processor.domain.TypeInfo;

import javax.lang.model.element.VariableElement;

public interface VariableElementParser {
    /**
     * If a dependent type is not explicitly registered by {@link org.jets.annotation.EmitType},
     * it may not have been resolved in the context by the time of the mapper call.
     * In such case, the caller would resolve the type in another iteration and map the type again.
     * <p>
     * An unresolved type is a type whose TypeElement hasn't been processed.
     * So there's no information, e.g. name, saved in global context yet.
     * </p>
     */
    TypeInfo parse(VariableElement element);

}

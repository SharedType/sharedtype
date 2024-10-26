package org.sharedtype.processor.parser.type;

import org.sharedtype.annotation.SharedType;
import org.sharedtype.domain.TypeInfo;

import javax.lang.model.type.TypeMirror;

public interface TypeInfoParser {
    /**
     * If a dependent type is not explicitly registered by {@link SharedType},
     * it may not have been resolved in the context by the time of the mapper call.
     * In such case, the caller would resolve the type in another iteration and map the type again.
     * <p>
     * An unresolved type is a type whose TypeElement hasn't been processed.
     * So there's no information, e.g. name, saved in global context yet.
     * </p>
     */
    TypeInfo parse(TypeMirror typeMirror);

}

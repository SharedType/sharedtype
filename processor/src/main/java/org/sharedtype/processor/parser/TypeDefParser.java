package org.sharedtype.processor.parser;

import org.sharedtype.domain.TypeDef;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;

public interface TypeDefParser {
    /**
     * @return null if the typeElement is ignored.
     */
    @Nullable
    TypeDef parse(TypeElement typeElement);
}

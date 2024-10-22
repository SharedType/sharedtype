package org.sharedtype.processor.parser;

import org.sharedtype.processor.domain.TypeDef;

import javax.lang.model.element.TypeElement;

public interface TypeDefParser {
    /**
     * @return a nonempty list containing the parsed type info and dependent type info if any. The first element of the list is of the given typeElement.
     */
    TypeDef parse(TypeElement typeElement);
}

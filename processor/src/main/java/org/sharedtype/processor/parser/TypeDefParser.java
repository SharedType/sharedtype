package org.sharedtype.processor.parser;

import java.util.List;

import javax.lang.model.element.TypeElement;

import org.sharedtype.processor.domain.TypeDef;

public interface TypeDefParser {
    /**
     *
     * @return a nonempty list containing the parsed type info and dependent type info if any. The first element of the list is of the given typeElement.
     */
    List<TypeDef> parse(TypeElement typeElement);
}

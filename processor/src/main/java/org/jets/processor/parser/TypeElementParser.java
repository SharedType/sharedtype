package org.jets.processor.parser;

import java.util.List;

import javax.lang.model.element.TypeElement;

import org.jets.processor.domain.DefInfo;

public interface TypeElementParser {
    /**
     *
     * @return a nonempty list containing the parsed type info and dependent type info if any. The first element of the list is of the given typeElement.
     */
    List<DefInfo> parse(TypeElement typeElement);
}

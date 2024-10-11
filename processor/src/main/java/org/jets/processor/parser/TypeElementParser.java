package org.jets.processor.parser;

import javax.lang.model.element.TypeElement;

import org.jets.processor.domain.ClassInfo;

public interface TypeElementParser {
    ClassInfo parse(TypeElement typeElement);
}

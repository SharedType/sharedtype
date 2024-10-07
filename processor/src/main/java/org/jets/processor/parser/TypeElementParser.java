package org.jets.processor.parser;

import org.jets.processor.JetsContext;

import javax.lang.model.element.TypeElement;

public interface TypeElementParser {
    TypeInfo parse(TypeElement typeElement, JetsContext ctx);
}

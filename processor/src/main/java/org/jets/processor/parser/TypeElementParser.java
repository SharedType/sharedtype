package org.jets.processor.parser;

import org.jets.processor.JetsContext;

import javax.lang.model.element.TypeElement;
import java.util.List;

public interface TypeElementParser {
    List<TypeInfo> parse(TypeElement typeElement, JetsContext ctx);
}

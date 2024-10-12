package org.jets.processor.parser;

import java.util.List;

import javax.lang.model.element.TypeElement;

import org.jets.processor.domain.DefInfo;

public interface TypeElementParser {
    List<DefInfo> parse(TypeElement typeElement);
}

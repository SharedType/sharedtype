package org.jets.processor.parser;

import javax.lang.model.element.TypeElement;

import org.jets.processor.domain.ClassInfo;
import org.jets.processor.domain.DefInfo;

import java.util.List;

public interface TypeElementParser {
    List<DefInfo> parse(TypeElement typeElement);
}

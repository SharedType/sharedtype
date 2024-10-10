package org.jets.processor.parser;

import org.jets.processor.JetsContext;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Map;

public interface TypeElementParser {
    TypeInfo parse(TypeElement typeElement, JetsContext ctx);

    static TypeElementParser getParser() {
        return new CompositeTypeElementParser(Map.of(
                ElementKind.RECORD, new JavaRecordParser()
        ));
    }
}

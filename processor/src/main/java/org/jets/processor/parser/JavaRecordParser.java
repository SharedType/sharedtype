package org.jets.processor.parser;

import org.jets.processor.JetsContext;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

final class JavaRecordParser implements TypeElementParser {
    @Override
    public List<TypeInfo> parse(TypeElement typeElement, JetsContext ctx) {
        checkArgument(typeElement.getKind() == ElementKind.RECORD, "Unsupported element kind: " + typeElement.getKind());


        return null;
    }
}

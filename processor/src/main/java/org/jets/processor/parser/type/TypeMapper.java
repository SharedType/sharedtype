package org.jets.processor.parser.type;

import javax.lang.model.type.TypeMirror;

import org.jets.processor.JetsContext;
import org.jets.processor.domain.TypeSymbol;

public interface TypeMapper {
  TypeSymbol map(TypeMirror typeMirror, JetsContext ctx);
}

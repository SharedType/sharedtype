package org.jets.processor.parser.mapper;

import javax.lang.model.type.TypeMirror;

import org.jets.processor.domain.TypeSymbol;

public interface TypeMapper {
  TypeSymbol map(TypeMirror typeMirror);
}

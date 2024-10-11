package org.jets.processor.parser.mapper;

import javax.lang.model.type.TypeMirror;

public interface TypeMapper {
  Result map(TypeMirror typeMirror);

  record Result(String javaQualifiedTypename, String typename) {
  }
}

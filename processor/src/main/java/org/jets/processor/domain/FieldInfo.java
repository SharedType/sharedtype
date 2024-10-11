package org.jets.processor.domain;

import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import lombok.Builder;

@Builder
public record FieldInfo(
  String name,
  Set<Modifier> modifiers,
  boolean optional,
  String javaQualifiedTypename,
  String typename,
  boolean typeResolved
) {
  
}

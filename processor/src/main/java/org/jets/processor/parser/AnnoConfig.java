package org.jets.processor.parser;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import lombok.Getter;
import org.jets.annotation.EmitType;

final class AnnoConfig {
  private final EmitType anno;
  @Getter
  private final String name;
  private final Set<String> excludes;

  AnnoConfig(TypeElement typeElement) {
    this.anno = typeElement.getAnnotation(EmitType.class);
    this.name = anno.name().isEmpty() ? typeElement.getSimpleName().toString() : anno.name();
    this.excludes = Set.of(anno.excludes());
  }

  boolean isComponentExcluded(Element element) {
    return excludes.contains(element.getSimpleName().toString());
  }

  boolean isIncludeGetters() {
    return anno.includeGetters();
  }
    
}

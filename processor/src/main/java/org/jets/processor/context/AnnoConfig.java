package org.jets.processor.context;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import lombok.Getter;
import org.jets.annotation.EmitType;

public final class AnnoConfig {
  private final EmitType anno;
  @Getter
  private final String name;
  @Getter
  private final String qualifiedName;
  private final Set<String> excludes;

  public AnnoConfig(TypeElement typeElement) {
    this.anno = typeElement.getAnnotation(EmitType.class);
    this.name = anno.name().isEmpty() ? typeElement.getSimpleName().toString() : anno.name();
    this.qualifiedName = typeElement.getQualifiedName().toString();
    this.excludes = Set.of(anno.excludes());
  }

  public boolean isComponentExcluded(Element element) {
    return excludes.contains(element.getSimpleName().toString());
  }

  public boolean toIncludeGetters() {
    return anno.includeAccessors();
  }
}

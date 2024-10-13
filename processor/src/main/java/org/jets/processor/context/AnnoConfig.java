package org.jets.processor.context;

import java.util.Collections;
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
    var simpleName = typeElement.getSimpleName().toString();
    this.anno = typeElement.getAnnotation(EmitType.class);
    if (anno == null) {
      this.name = simpleName;
      this.excludes = Collections.emptySet();
    } else {
      this.name = anno.name().isEmpty() ? simpleName : anno.name();
      this.excludes = Set.of(anno.excludes());
    }

    this.qualifiedName = typeElement.getQualifiedName().toString();
  }

  public boolean isComponentExcluded(Element element) {
    return excludes.contains(element.getSimpleName().toString());
  }

  public boolean toIncludeGetters() {
    return anno.includeAccessors();
  }
}

package org.sharedtype.processor.context;

import lombok.Getter;
import org.sharedtype.annotation.SharedType;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Config {
  private final SharedType anno;
  @Getter
  private final String name;
  @Getter
  private final String qualifiedName;

  @Retention(RetentionPolicy.RUNTIME)
  private @interface AnnoContainer {
    SharedType anno() default @SharedType;
  }

  @AnnoContainer
  private record DummyDefault() {}

  public Config(TypeElement typeElement) {
    var simpleName = typeElement.getSimpleName().toString();
    var annoFromType = typeElement.getAnnotation(SharedType.class);
    this.anno = annoFromType == null ? DummyDefault.class.getAnnotation(AnnoContainer.class).anno() : annoFromType;
    this.name = anno.name().isEmpty() ? simpleName : anno.name();
    this.qualifiedName = typeElement.getQualifiedName().toString();
  }

  public boolean isComponentIgnored(Element element) {
    var ignored = element.getAnnotation(SharedType.Ignore.class);
    return ignored != null;
  }

  public boolean toIncludeAccessors() {
    return anno.includeAccessors();
  }
}

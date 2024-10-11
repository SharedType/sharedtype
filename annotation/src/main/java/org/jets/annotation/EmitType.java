package org.jets.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
public @interface EmitType {
  /**
   * The name of the emitted type. If not specified, the simple name of the annotated type will be used.
   */
  String name() default "";

  /**
   * Whether to include getters in POJO or accessors in record.
   * Methods starting with 'get' or 'is' with 0 arguments in POJO or methods with 0 arguments in record will be included.
   * To exclude, use {@link #excludes()}
   */
  boolean includeGetters() default false;

  /**
   * Exclude components in a type.
   * Components include methods, fields, and record components.
   * @return the names of components to exclude.
   */
  String[] excludes() default {};
}

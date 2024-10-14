package org.sharedtype.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Inner class:</b>
 * <p>
 * Declared inner and nested types will not be included by default, unless they are referenced by other types.
 *     TODO: doc for nested types
 * </p>
 *
 * @author Cause Chung
 */
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
    boolean includeAccessors() default false;

    /**
     * Exclude fields in a type. Fields include methods(when {@link #includeAccessors()} is true), fields, and record components.
     * <br>
     * <b>Fields in supertypes:</b>
     * <p>
     *     Inherited fields will be not be included in this type by default (see {@link #flattenInheritance()}),
     *     they will be included in supertypes,
     *     in this case, add {@link EmitType} to supertypes to exclude them.
     *     However, when {@link #flattenInheritance()} is true, supertypes will not be emitted,
     *     so, to exclude fields in supertypes, add them in this option as well.
     * </p>
     *
     * @return the names of components to exclude.
     */
    String[] excludes() default {};

    /**
     * Whether to flatten and include all inherited components from super type(s) in this type.
     * TODO: doc and implementation
     */
    boolean flattenInheritance() default false;
}

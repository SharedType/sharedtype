package org.sharedtype.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
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
public @interface SharedType {
    /**
     * The name of the emitted type. If not specified, the simple name of the annotated type will be used.
     */
    String name() default "";

    /**
     * Whether to include getters in POJO or accessors in record.
     * Methods starting with 'get' or 'is' with 0 arguments in POJO or methods with 0 arguments in record will be included.
     * To exclude a particular one, use {@link Ignore}
     */
    boolean includeAccessors() default false;

    /**
     * Whether to flatten and include all inherited components from super type(s) in this type.
     * When is true, supertypes will not be emitted.
     */
    boolean flattenInheritance() default false;

    /**
     * Exclude fields, record components, accessors in a type. Or ignore a dependent type, e.g. a supertype.
     * <p>
     *     <b>When placed on type:</b> a subtype of this type will not include inherited members from this type.
     *     But if this type is referenced directly as type of a field or return type of an accessor, an error will be reported,
     *     unless the field or accessor is also ignored.
     * </p>
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Ignore {
    }
}

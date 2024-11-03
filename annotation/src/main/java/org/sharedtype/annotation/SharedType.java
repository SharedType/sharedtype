package org.sharedtype.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Share a type. <a href="https://github.com/cuzfrog/SharedType">Website</a></p>
 * <br>
 * <b>Inner class:</b>
 * <p>
 * Declared inner and nested types will not be included by default, unless they are referenced by other types.
 * Non-static inner classes are not supported, see documentation for details.
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
     * <p>
     *     The name of the emitted type. If not specified, the simple name of the annotated type will be used.
     *     This may be used to help avoid conflicting names in target output.
     * </p>
     * <br>
     * <p>
     *     How possibly conflicting names are resolved:
     *     <ul>
     *         <li>Typescript: simple name of a class is used as type name. Duplicate names are not allowed.</li>
     *     </ul>
     * </p>
     */
    String name() default "";

    /**
     * Includes fields, record components, accessors, or constants in a type.
     * <p>
     * To exclude a particular component, use {@link Ignore}.
     * </p>
     * <br>
     * <p>
     * Fields and accessors duplicates resolution:
     *     <ul>
     *         <li>In classes, fields and accessors effectively with the same name will be merged.</li>
     *         <li>In records, when accessors are included, records components are ignored.</li>
     *     </ul>
     * </p>
     *
     * @see ComponentType
     */
    ComponentType[] includes() default {ComponentType.FIELDS, ComponentType.ACCESSORS};

    /**
     * Mark a method as an accessor regardless of its name. This annotation will be ignored if {@link #includes()} does not include {@link ComponentType#ACCESSORS}.
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Accessor {
    }

    /**
     * Exclude fields, record components, accessors in a type. Or ignore a dependent type, e.g. a supertype.
     * <p>
     * <b>When placed on type:</b> a subtype of this type will not include inherited members from this type.
     * But if this type is referenced directly as type of a field or return type of an accessor, an error will be reported,
     * unless the field or accessor is also ignored.
     * </p>
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Ignore {
    }

    /**
     * Mark enum value. By default, enum value is the enum constant name. The enum value must be literals (e.g. 1, "a", true) in enum constant expressions.
     * <br>
     * <p>
     * When placed on:
     *  <ul>
     *      <li>Constructor parameter - the literal value served to this parameter from enum constant expressions will be used.</li>
     *      <li>Field - the constructor parameter with the same name and type will be used as if constructor parameter is annotated.</li>
     *  </ul>
     * </p>
     * <br>
     * <p>
     * Below are some valid examples:
     * </p>
     * <pre>
     * {@code
     * enum Enum {
     *   A(1), B(2);
     *
     *   @SharedType.EnumValue
     *   private final int value;
     *
     *   Enum(int value) {
     *     this.value = value;
     *   }
     * }
     * }
     * </pre>
     * is equivalent to:
     * <pre>
     * {@code
     * enum Enum {
     *   A(1), B(2);
     *
     *   private final int value;
     *
     *   Enum(@SharedType.EnumValue int value) {
     *     this.value = value;
     *   }
     * }
     * }
     * </pre>
     */
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.CLASS)
    @interface EnumValue {
    }

    enum ComponentType {
        /**
         * Represents:
         * <ul>
         *     <li>Class instance fields.</li>
         *     <li>Record components.</li>
         * </ul>
         */
        FIELDS,
        /**
         * Represents 0 argument non-static methods:
         * <ul>
         *     <li>with name same as its instance field. Or fluent getter. This includes record's component accessor.</li>
         *     <li>starting with a getter prefix. By default, prefixes include 'get' or 'is', which can be configured via global properties.</li>
         *     <li>annotated with {@link Accessor}.</li>
         * </ul>
         */
        ACCESSORS,
        /**
         * Represents:
         * <ul>
         *     <li>Class/record static fields with static values.</li>
         * </ul>
         * Fields with values that cannot be resolved at compile time will not be included. A corresponding warning will be given.
         */
        CONSTANTS,
    }
}

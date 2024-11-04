package org.sharedtype.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Mark a class, record, enum, or interface for target code generation.</p>
 *
 * <p>
 * <b>Annotation Processing:</b><br>
 * This annotation is retained only in source code.
 * That means it is not visible if source code is not directly participating in annotation processing.
 * E.g. if it is in a dependency jar in a project with multi-module build.
 * <br>
 * <br>
 * When used together with <b>Lombok</b>, the processing order matters.
 * If processed before Lombok, SharedType will see only the original source code.
 * E.g. if getters are handled by Lombok @Getter, SharedType will not see the getter method.
 * It's recommended to execute SharedType before Lombok, since Lombok does not provide extra info to SharedType.
 * Also you may only want a fast execution of SharedType with "-proc:only" without other annotation processors.
 * </p>
 *
 * <p>
 * <b>Configurations:</b><br>
 * Properties on class level (via this annotation) will take precedence over global properties.
 * Properties that only apply to global level will not be present on class level.
 * </p>
 *
 * <p>
 * <b>Inner class:</b><br>
 * Declared inner and nested types will not be included by default, unless they are referenced by other types.
 * Non-static inner classes are not supported.
 * </p>
 *
 * <p>See documentation for details. <a href="https://github.com/cuzfrog/SharedType">SharedType Website</a></p></p>
 *
 * @author Cause Chung
 */
@Retention(RetentionPolicy.SOURCE)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
public @interface SharedType {
    /**
     * <p>
     * The name of the emitted type. If not specified, the simple name of the annotated type will be used.
     * This may be used to help avoid conflicting names in target output.
     * </p>
     * <br>
     * <p>
     * How conflicting names are resolved:
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
     * <p>
     * Fields and accessors duplicates resolution:
     *     <ul>
     *         <li>In classes, fields and accessors effectively with the same name will be merged.</li>
     *         <li>Same for record.</li>
     *     </ul>
     * </p>
     *
     * @see ComponentType#FIELDS
     * @see ComponentType#ACCESSORS
     * @see ComponentType#CONSTANTS
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

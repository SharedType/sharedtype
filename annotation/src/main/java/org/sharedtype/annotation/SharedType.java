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
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Ignore {
    }

    /**
     * Mark enum value. By default, enum value is the enum constant name.
     * <br>
     * When placed on:
     * <ul>
     *     <li>Constructor parameter - the literal value served to this parameter of enum constants will be used.</li>
     *     <li>Field - the constructor parameter with the same name and type will be used as if constructor parameter is annotated.</li>
     * </ul>
     */
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    @interface EnumValue {
    }

    enum ComponentType {
        /**
         * Represents:
         * <ul>
         *     <li>Class fields.</li>
         *     <li>Record components</li>
         * </ul>
         */
        FIELDS,
        /**
         * Represents:
         * <ul>
         *     <li>In a class or enum, methods starting with a getter prefix and 0 arguments. By default, prefixes include 'get' or 'is'.</li>
         *     <li>In a record, only components' accessors.</li>
         *     <li>Methods annotated with {@link Accessor} in both class and records</li>
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

package online.sharedtype;

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
 * <b>Inheritance:</b>
 * In different target schemas, inheritance may be transferred differently.
 * <ul>
 *     <li>Typescript: Inheritance is mimicked. A subtype's inherited properties will not be included in its own declared properties,
 *     while the supertype is also emitted. </li>
 *     <li>Rust: Inheritance is not supported.
 *     Type hierarchy will be flattened that a subtype's inherited properties will be included in its own declared properties,
 *     while, by default, any supertypes will not be emitted again.</li>
 * </ul>
 *
 * <p>
 * <b>Inner classes:</b><br>
 * Declared inner and nested types will not be included by default, unless they are referenced by other types.
 * Non-static inner classes are not supported.
 * </p>
 *
 * <p>
 * <b>Cyclic Reference:</b>
 * <ul>
 *     <li>Rust: Cyclic references will be wrapped in {@code Option<Box<T>>}.</li>
 * </ul>
 *
 * <p>
 * <b>Constants:</b><br>
 * Support is planned in upcoming versions. Only compile-time resolvable values or their combinations are supported.
 * </p>
 *
 * <p>
 * <b>Generics:</b><br>
 * Generics are supported. But it's also limited to target schema's capacity. Currently, type bounds are not supported.
 * </p>
 *
 * <p>
 * <b>Collections:</b><br>
 * Iterables and arrays are treated as arrays and, by default, are mapped to:
 * <ul>
 *     <li>Typescript: {@code T[]}</li>
 *     <li>Rust: {@code Vec<T>}</li>
 * </ul>
 *
 * <p>
 * <b>Maps:</b>
 * Key must be String or numeric types. Enum is support given that its value is a literal.
 * <ul>
 *     <li>Typescript: e.g. {@code Record<string, T>} where {@code T} can be a reified type. If the key is enum, it will be a {@code Partial<Record<?, ?>>}</li>
 * </ul>
 *
 * <p><a href="https://github.com/cuzfrog/SharedType">SharedType Website</a></p>
 *
 * @author Cause Chung
 * @implNote generics type bounds are not supported yet, Map is not supported yet.
 */
// TODO: test user-defined array-like types
@Retention(RetentionPolicy.SOURCE)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
public @interface SharedType {
    /**
     * <p>
     * The name of the emitted type. If not specified, the simple name of the annotated type will be used.
     * This may be used to help avoid conflicting names in target code.
     * </p>
     * <br>
     * <p>
     * How conflicting names are resolved:
     * <ul>
     *     <li>Typescript: simple name of a class is used as type name. Duplicate names are not allowed.</li>
     * </ul>
     */
    String name() default "";

    /**
     * <p>
     * Configure whether to include fields, record components, accessors, or constants in a type.
     * All included by default.
     * </p>
     * <p>
     * To exclude a particular component, use {@link Ignore}.
     * Fields and accessors effectively with the same name will be merged.
     * </p>
     *
     * @see ComponentType#FIELDS
     * @see ComponentType#ACCESSORS
     * @see ComponentType#CONSTANTS
     */
    ComponentType[] includes() default {ComponentType.FIELDS, ComponentType.ACCESSORS, ComponentType.CONSTANTS};

    /**
     * Macros to be added to the type in generated Rust code. E.g. "Debug" will generate {@code #[derive](Debug)}.
     * This property only affects this annotated type. By default, "Debug" and "PartialEq" are added to all types, which can be configured via global properties.
     * Resulted macros contain both global default and this property.
     *
     * @return Rust macro traits. E.g. "serde::Serialize".
     */
    String[] rustMacroTraits() default {};

    /**
     * Mark a method as an accessor regardless of its name.
     * Getter prefixes are configured in global properties.
     * This annotation will be ignored if {@link #includes()} does not include {@link ComponentType#ACCESSORS}.
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Accessor {
    }

    /**
     * Exclude fields, record components, accessors in a type, or a dependency type, e.g. a supertype.
     * <p>
     * <b>When placed on a type:</b> a subtype of this type will not extend this type in target code.
     * But if this type is referenced directly as type of field or return type of accessor, a compilation error will be reported,
     * unless the field or accessor is also ignored.
     * </p>
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Ignore {
    }

    /**
     * Mark enum value. By default, enum value is the enum constant name. The enum value must be literals (e.g. 1, "a", true) in enum constant expressions.
     * <p>
     * When placed on:
     *  <ul>
     *      <li>Constructor parameter - the literal value served to this parameter from enum constant expressions will be used.</li>
     *      <li>Field - the constructor parameter with the same name and type will be used as if constructor parameter is annotated.</li>
     *  </ul>
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
         * Represents 0 argument non-static methods that:
         * <ul>
         *     <li>have names same as respective instance fields, aka, fluent getter. This includes record's component accessor.</li>
         *     <li>start with a getter prefix. By default, prefixes include 'get' or 'is', which can be configured via global properties.</li>
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
         *
         * @implNote not implement yet.
         */
        CONSTANTS,
    }
}

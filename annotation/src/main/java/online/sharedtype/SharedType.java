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
 * This annotation is retained only at source code level.
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
 * <b>Serialization:</b><br>
 * Although SharedType does not depend on any serialization format, it assumes the contract of JSON.
 * That means a client can use any format of serializations, but may not exceed JSON capacity.
 * E.g. SharedType treats {@code List} and {@code Set} as arrays, and will not differentiate them in target code.
 * </p>
 *
 * <p>
 * <b>Configurations:</b><br>
 * Global properties can be defined in a client provided property file or system properties.
 * By default, SharedType will look for file name "sharedtype.properties" on cmd path.
 * The property file path can be changed via compiler option "sharedtype.propsFile", see web for details.
 * System properties will override property file values.
 * Some properties are also defined at class level, i.e. via this annotation, which will take precedence over global properties.
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
 *     <li>Typescript: Cyclic referenced field will be optional.</li>
 *     <li>Rust: Cyclic references will be wrapped in {@code Option<Box<T>>}.</li>
 * </ul>
 *
 * <p>
 * <b>Enums:</b><br>
 * Enums are emitted as below:
 * <ul>
 *     <li>Typescript: type union or enum. For simple enums, values are enum constants' names.</li>
 *     <li>Rust: plain enum for simple enums; impl a const fun {@code value()} for custom enum values.</li>
 * </ul>
 * See {@link EnumValue} for how to mark an enum value.
 *
 * <p>
 * <b>Constants:</b><br>
 * Static fields are treated as constants.
 * By default, constants are not included, see {@link #includes()}.
 * Only constants in explicitly annotated types will be emitted.
 * <br>
 * Only compile-time resolvable values are supported, which include:
 * <ul>
 *     <li>literal values: primitives, boxed primitives, String</li>
 *     <li>enums (with compile-time resolvable values)</li>
 *     <li>references that eventually reach other compile-time resolvable values</li>
 * </ul>
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
 * <b>Optional:</b><br>
 * Optionals can be marked by either annotations or an optional types, both can be configured via global properties.
 * Optional types have a more complicated mapping, where only the outermost optional is recognized as the optional marker,
 * any nested optional types are flattened, similar to Jackson's serialization behavior.
 * E.g. a type {@code Optional<Optional<List<Optional<String>>>} can be emitted as:
 * <ul>
 *     <li>Typescript: {@code String[] | undefined}</li>
 *     <li>Rust: {@code Option<Vec<String>>}</li>
 * </ul>
 *
 * <p>
 * <b>Maps:</b><br>
 * Key must be String or numeric types. Enum is support given that its value is a literal.
 * Custom map types are supported, e.g. a class that extends HashMap. But the type itself is treated as a mapType, so its structure will not be emitted.
 * <ul>
 *     <li>Typescript: e.g. {@code Record<string, T>} where {@code T} can be a reified type. If the key is enum, it will be a {@code Partial<Record<?, ?>>}</li>
 *     <li>Rust: e.g. {@code HashMap<String, T>}, {@code HashMap<EnumType, T>}</li>
 * </ul>
 *
 * <p>
 * <b>Date and Time:</b><br>
 * By default, types that are recognized as date and time are emitted as strings, date/time types are configured via global properties.
 * The emitted target type can be configured via global properties or via this annotation.
 * Target type can be any type literal, but SharedType will not verify its validity in emitted code.
 * </p><br>
 *
 * <p>
 * <b>Math types:</b><br>
 * By default {@link java.math.BigInteger} and {@link java.math.BigDecimal} are emitted as strings.
 * A client can use type mappings to override the emitted types.
 * <br>
 * Note: Constants' values with math types are converted to strings. Custom mapping of math types will not affect constant value type.
 * See "Constants" section for details.
 * </p><br>
 *
 * <p>
 * <b>Type literal mappings:</b><br>
 * You dan define any 1-to-1 type mappings via global properties. This is useful when e.g. a 3rd party type is referenced.
 * Note, SharedType will still try to resolve the type and emit it with the mapped name.
 * If you don't want SharedType to auto-resolve and emit it, mark it as ignored via {@link SharedType.Ignore} or global properties.
 * <br>
 * Type mapped this way will take the highest precedence.
 * E.g. a date type is configured to be emitted as string, you can override the particular mapping to emit a {@code Date}.
 * Type mapping will override name configured in {@link SharedType#name()}.
 * </p><br>
 *
 * <p>
 * <b>Custom code snippet:</b><br>
 * Clients can provide custom code snippets to be injected into the emitted file.
 * This can be useful when e.g. a 3rd party type is referenced at client code.
 * By default, SharedType will search files "sharedtype-custom-code.ts", "sharedtype-custom-code.rs" respectively on cmd path.
 * File paths can be configured via global properties.
 * </p>
 *
 * <br>
 * <p><a href="https://github.com/SharedType/sharedtype">SharedType Website</a></p>
 *
 * @author Cause Chung
 * @implNote generics type bounds are not supported yet.
 */
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
     * This is similar to but different from type mappings configured via global properties,
     * which maps any Java type to a target type. When both are defined, this will be overridden by type mappings.
     * See {@link SharedType} for details.
     */
    String name() default "";

    /**
     * <p>
     * Configure whether to include fields, record components, accessors, or constants in a type.
     * </p>
     * <p>
     * To exclude a particular component, use {@link Ignore}.
     * Fields and accessors with the same name and type will be merged.
     * </p>
     *
     * @see ComponentType#FIELDS
     * @see ComponentType#ACCESSORS
     * @see ComponentType#CONSTANTS
     */
    ComponentType[] includes() default {ComponentType.FIELDS, ComponentType.ACCESSORS};

    /**
     * Java fields have to reside in a class, which provides a natural namespace.
     * By default, the generated constants will be put in a namespace with the same name as the class.
     * <ul>
     *     <li>Typescript - constants be put in a const object</li>
     *     <li>Rust - constants be put in a mod</li>
     * </ul>
     * This default can be configured via global properties.
     *
     * @return Whether to inline typescript constants to top level without an object as namespace.
     */
    OptionalBool constantNamespaced() default OptionalBool.DEFAULT;

    /**
     * Macros to be added to the type in generated Rust code. E.g. "Debug" will generate {@code #[derive](Debug)}.
     * This property only affects this annotated type. By default, "Debug" and "PartialEq" are added to all types, which can be configured via global properties.
     * Resulted macros contain both global default and this property.
     *
     * @return Rust macro traits. E.g. "serde::Serialize".
     */
    String[] rustMacroTraits() default {};

    /**
     * Type literal to be emitted for date/time types. How a java type is considered a date/time type is defined by global properties.
     *
     * @return any literal, e.g. "String", "chrono::DateTime". When empty, fallback to global default.
     */
    String rustTargetDatetimeTypeLiteral() default "";

    /**
     * How to render optional fields in Typescript.
     *
     * @return combination of "?", "null", "undefined", the latter 2 are rendered as union types. If empty, fallback to global default.
     */
    String[] typescriptOptionalFieldFormat() default {};

    /**
     * Format of enum in Typescript.
     *
     * @return one of "const_enum" (const enum), "enum" (enum), or "union" (union types). If empty, fallback to global default.
     */
    String typescriptEnumFormat() default "";

    /**
     * Whether to mark generated type fields as readonly. Default fallback to global properties.
     *
     * @return value can be one of:
     * "all" - all fields are readonly
     * "acyclic" - only fields of not cyclic-referenced types are readonly
     * "none" - no fields are readonly
     */
    String typescriptFieldReadonlyType() default "";

    /**
     * Type literal to be emitted for date/time types. How a java type is considered a date/time type is defined by global properties.
     *
     * @return any literal, e.g. "string", "Date". When empty, fallback to global default.
     */
    String typescriptTargetDatetimeTypeLiteral() default "";

    /**
     * Type literal to be emitted for date/time types. How a java type is considered a date/time type is defined by global properties.
     * @return any literal, e.g. "string", "Date". When empty, fallback to global default.
     */
    String goTargetDatetimeTypeLiteral() default "";

    /**
     * Mark a method as an accessor regardless of its name.
     * Getter prefixes are configured in global properties.
     * This annotation will be ignored if {@link #includes()} does not include {@link ComponentType#ACCESSORS}.
     * <br>
     * Additional annotation types can be configured via global properties.
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Accessor {
    }

    /**
     * Exclude fields, record components, accessors in a type, or a dependency type, e.g. a supertype.
     * <p>
     * <b>When placed on a type:</b><br>
     * This type will not be emitted.
     * If this type has a subtype that is going to be emitted, the subtype will not extend this type and its members in target code.
     * But if this type is referenced directly as type of field or return type of accessor, and "safe-type-resolution" is enabled,
     * a compilation error will be reported, unless the field or accessor is also ignored.
     * </p>
     * <br>
     * Additional annotation types can be configured via global properties.
     */
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Ignore {
    }

    /**
     * Mark enum value. By default, enum value is the enum constant name.
     * The enum value must be "compile-time resolvable" in enum constant expressions (See "Constant" section in {@link SharedType}).
     * Note: When there are multiple enum constant constructor parameters, the value is resolved by field order.
     * If the constructor parameter order is different from the field order, value will not be resolved correctly.
     * Additional custom annotation types can be configured via global properties.
     * <pre>
     * {@code
     * //A simple example:
     * enum Enum {
     *   A(1), B(2);
     *
     *   @SharedType.EnumValue
     *   private final int value;
     * }
     * //output in typescript union:
     * type Enum = 1 | 2;
     * }
     * </pre>
     * <br>
     */
    @Target({ElementType.FIELD})
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
         *     <li>annotated with {@link Accessor} or any custom annotations configured via global properties.</li>
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

    enum OptionalBool {
        TRUE,
        FALSE,
        /**
         * Fallback to global default.
         */
        DEFAULT,
    }
}

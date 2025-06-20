package online.sharedtype.processor.context;

import lombok.Getter;
import online.sharedtype.SharedType;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static online.sharedtype.processor.support.utils.Utils.notEmptyOrDefault;

/**
 * Config wrappers.
 *
 * @author Cause Chung
 */
@Getter
public final class Config {
    private final SharedType anno;
    private final boolean annotated;
    private final String simpleName;
    private final String qualifiedName;
    private final Set<SharedType.ComponentType> includedComponentTypes;
    private final boolean constantNamespaced;
    private final Set<Props.Typescript.OptionalFieldFormat> typescriptOptionalFieldFormats;
    private final Props.Typescript.EnumFormat typescriptEnumFormat;
    private final Props.Typescript.FieldReadonlyType typescriptFieldReadonly;
    private final Props.Go.EnumFormat goEnumFormat;
    private final String typescriptTargetDatetimeTypeLiteral;
    private final String goTargetDatetimeTypeLiteral;
    private final String rustTargetDatetimeTypeLiteral;
    private final String rustConstKeyword;

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnoContainer {
        SharedType anno() default @SharedType;
    }

    @AnnoContainer
    static final class DummyDefault {
    }

    public Config(TypeElement typeElement, Context ctx) {
        String simpleName = typeElement.getSimpleName().toString();
        SharedType annoFromType = typeElement.getAnnotation(SharedType.class);
        this.annotated = annoFromType != null;
        this.anno = annoFromType == null ? DummyDefault.class.getAnnotation(AnnoContainer.class).anno() : annoFromType;
        this.simpleName = anno.name().isEmpty() ? simpleName : anno.name();
        this.qualifiedName = typeElement.getQualifiedName().toString();
        List<SharedType.ComponentType> includedCompTypes = Arrays.asList(anno.includes());
        this.includedComponentTypes = includedCompTypes.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(includedCompTypes);
        constantNamespaced = evaluateOptionalBool(anno.constantNamespaced(), ctx.getProps().isConstantNamespaced());
        typescriptOptionalFieldFormats = parseTsOptionalFieldFormats(anno, ctx);
        typescriptEnumFormat = parseTsEnumFormat(anno, ctx);
        typescriptFieldReadonly = parseTsFieldReadonlyType(anno, ctx);
        goEnumFormat = parseGoEnumFormat(anno, ctx);
        typescriptTargetDatetimeTypeLiteral = notEmptyOrDefault(
            anno.typescriptTargetDatetimeTypeLiteral(),
            ctx.getProps().getTypescript().getTargetDatetimeTypeLiteral(),
            () -> String.format("Loading typescriptTargetDatetimeTypeLiteral failed. Please check your configuration for '%s'", qualifiedName)
        );
        goTargetDatetimeTypeLiteral = notEmptyOrDefault(
            anno.goTargetDatetimeTypeLiteral(),
            ctx.getProps().getGo().getTargetDatetimeTypeLiteral(),
            () -> String.format("Loading goTargetDatetimeTypeLiteral failed. Please check your configuration for '%s'", qualifiedName)
        );
        rustTargetDatetimeTypeLiteral = notEmptyOrDefault(
            anno.rustTargetDatetimeTypeLiteral(),
            ctx.getProps().getRust().getTargetDatetimeTypeLiteral(),
            () -> String.format("Loading rustTargetDatetimeTypeLiteral failed. Please check your configuration for '%s'", qualifiedName)
        );
        rustConstKeyword = validateRustConstKeyword(anno.rustConstKeyword());
    }

    public boolean includes(SharedType.ComponentType componentType) {
        return includedComponentTypes.contains(componentType);
    }


    private static boolean evaluateOptionalBool(SharedType.OptionalBool optionalBool, boolean defaultValue) {
        if (optionalBool == SharedType.OptionalBool.TRUE) {
            return true;
        } else if (optionalBool == SharedType.OptionalBool.FALSE) {
            return false;
        } else {
            return defaultValue;
        }
    }

    private Set<Props.Typescript.OptionalFieldFormat> parseTsOptionalFieldFormats(SharedType anno, Context ctx) {
        if (anno.typescriptOptionalFieldFormat().length > 0) {
            List<String> values = Arrays.asList(anno.typescriptOptionalFieldFormat());
            try {
                return EnumParsingUtils.parseEnumSet(values, Props.Typescript.OptionalFieldFormat.class, Props.Typescript.OptionalFieldFormat::fromString);
            } catch (IllegalArgumentException e) {
                throw new SharedTypeException(String.format(
                    "Invalid value for SharedType.typescriptOptionalFieldFormat: %s, only '?', 'null', 'undefined' are allowed. " +
                        "When parsing annotation for '%s'.", values, qualifiedName), e);
            }
        }
        return ctx.getProps().getTypescript().getOptionalFieldFormats();
    }

    private Props.Typescript.EnumFormat parseTsEnumFormat(SharedType anno, Context ctx) {
        if (anno.typescriptEnumFormat() != null && !anno.typescriptEnumFormat().isEmpty()) {
            try {
                return Props.Typescript.EnumFormat.fromString(anno.typescriptEnumFormat());
            } catch (IllegalArgumentException e) {
                throw new SharedTypeException(String.format(
                    "Invalid value for SharedType.typescriptEnumFormat: '%s', only one of 'union', 'const_enum', 'enum' is allowed. " +
                        "When parsing annotation for '%s'.", anno.typescriptEnumFormat(), qualifiedName), e);
            }
        }
        return ctx.getProps().getTypescript().getEnumFormat();
    }

    private Props.Typescript.FieldReadonlyType parseTsFieldReadonlyType(SharedType anno, Context ctx) {
        if (anno.typescriptFieldReadonlyType() != null && !anno.typescriptFieldReadonlyType().isEmpty()) {
            try {
                return Props.Typescript.FieldReadonlyType.fromString(anno.typescriptFieldReadonlyType());
            } catch (IllegalArgumentException e) {
                throw new SharedTypeException(String.format(
                    "Invalid value for SharedType.typescriptFieldReadonlyType: '%s', only 'all', 'acyclic', 'none' is allowed. " +
                        "When parsing annotation for '%s'.", anno.typescriptFieldReadonlyType(), qualifiedName), e);
            }
        }
        return ctx.getProps().getTypescript().getFieldReadonlyType();
    }

    private Props.Go.EnumFormat parseGoEnumFormat(SharedType anno, Context ctx) {
        if (anno.goEnumFormat() != null && !anno.goEnumFormat().isEmpty()) {
            try {
                return Props.Go.EnumFormat.fromString(anno.goEnumFormat());
            } catch (IllegalArgumentException e) {
                throw new SharedTypeException(String.format(
                    "Invalid value for SharedType.goEnumFormat: '%s', only 'const' or 'struct' is allowed. " +
                        "When parsing annotation for '%s'.", anno.goEnumFormat(), qualifiedName), e);
            }
        }
        return ctx.getProps().getGo().getEnumFormat();
    }

    private static String validateRustConstKeyword(String rustConstKeyword) {
        if (!rustConstKeyword.equals("const") && !rustConstKeyword.equals("static")) {
            throw new SharedTypeException(String.format(
                "Invalid value for SharedType.rustConstKeyword: '%s', only 'const' or 'static' is allowed.", rustConstKeyword));
        }
        return rustConstKeyword;
    }
}

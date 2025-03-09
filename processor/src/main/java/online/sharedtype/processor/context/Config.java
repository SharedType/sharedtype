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
import java.util.function.Function;

/**
 * Config wrappers.
 *
 * @author Cause Chung
 */
public final class Config {
    @Getter
    private final SharedType anno;
    @Getter
    private final String simpleName;
    @Getter
    private final String qualifiedName;
    private final Set<SharedType.ComponentType> includedComponentTypes;
    @Getter
    private final boolean constantNamespaced;
    @Getter
    private final Set<Props.Typescript.OptionalFieldFormat> typescriptOptionalFieldFormats;
    @Getter
    private final Props.Typescript.EnumFormat typescriptEnumFormat;

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
        this.anno = annoFromType == null ? DummyDefault.class.getAnnotation(AnnoContainer.class).anno() : annoFromType;
        this.simpleName = anno.name().isEmpty() ? simpleName : anno.name();
        this.qualifiedName = typeElement.getQualifiedName().toString();
        List<SharedType.ComponentType> includedCompTypes = Arrays.asList(anno.includes());
        this.includedComponentTypes = includedCompTypes.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(includedCompTypes);
        constantNamespaced = evaluateOptionalBool(anno.constantNamespaced(), ctx.getProps().isConstantNamespaced());
        typescriptOptionalFieldFormats = parseTsOptionalFieldFormats(anno, ctx);
        typescriptEnumFormat = parseTsEnumFormat(anno, ctx);
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

    private static Set<Props.Typescript.OptionalFieldFormat> parseTsOptionalFieldFormats(SharedType anno, Context ctx) {
        if (anno.typescriptOptionalFieldFormat().length > 0) {
            List<String> values = Arrays.asList(anno.typescriptOptionalFieldFormat());
            try {
                return EnumParsingUtils.parseEnumSet(values, Props.Typescript.OptionalFieldFormat.class, Props.Typescript.OptionalFieldFormat::fromString);
            } catch (IllegalArgumentException e) {
                throw new SharedTypeException(String.format(
                    "Invalid value for SharedType.typescriptOptionalFieldFormat: %s, only '?', 'null', 'undefined' are allowed.", values), e);
            }
        }
        return ctx.getProps().getTypescript().getOptionalFieldFormats();
    }

    private static Props.Typescript.EnumFormat parseTsEnumFormat(SharedType anno, Context ctx) {
        if (anno.typescriptEnumFormat() != null && !anno.typescriptEnumFormat().isEmpty()) {
            try {
                return Props.Typescript.EnumFormat.fromString(anno.typescriptEnumFormat());
            } catch (IllegalArgumentException e) {
                throw new SharedTypeException(String.format(
                    "Invalid value for SharedType.typescriptEnumFormat: '%s', only 'union' or 'enum' is allowed.", anno.typescriptEnumFormat()), e);
            }
        }
        return ctx.getProps().getTypescript().getEnumFormat();
    }
}

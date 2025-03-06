package online.sharedtype.processor.context;

import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

/**
 * Global properties loader.
 *
 * @author Cause Chung
 */
public final class PropsFactory {
    private static final String DEFAULT_PROPERTIES_FILE = "sharedtype-default.properties";

    public static Props loadProps(@Nullable Path userPropertiesFile) {
        ClassLoader classLoader = PropsFactory.class.getClassLoader();
        try (InputStream defaultPropsInputstream = classLoader.getResourceAsStream(DEFAULT_PROPERTIES_FILE);
             InputStream userPropsInputstream = userPropertiesFile == null || Files.notExists(userPropertiesFile) ? null : Files.newInputStream(userPropertiesFile)) {
            Properties properties = new Properties();
            properties.load(defaultPropsInputstream);
            if (userPropsInputstream != null) {
                properties.load(userPropsInputstream);
            }
            Props props = loadProps(properties);
            if (props.getTypescript().getOptionalFieldFormats().isEmpty()) {
                throw new IllegalArgumentException("Props 'typescript.optional-field-format' cannot be empty.");
            }
            return props;
        } catch (Exception e) {
            throw new SharedTypeException("Failed to load properties.", e);
        }
    }

    private static Props loadProps(Properties properties) {
        return Props.builder()
            .targets(parseEnumSet(properties, "sharedtype.targets", OutputTarget.class, OutputTarget::valueOf))
            .optionalAnnotations(parseClassSet(properties, "sharedtype.optional-annotations"))
            .optionalContainerTypes(splitArray(properties.getProperty("sharedtype.optional-container-types")))
            .accessorGetterPrefixes(splitArray(properties.getProperty("sharedtype.accessor.getter-prefixes")))
            .arraylikeTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.array-like-types")))
            .maplikeTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.map-like-types")))
            .ignoredTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.ignored-types")))
            .ignoredFieldNames(splitArray(properties.getProperty("sharedtype.ignored-fields")))
            .constantNamespaced(parseBoolean(properties, "sharedtype.constant-namespaced"))
            .typescript(Props.Typescript.builder()
                .outputFileName(properties.getProperty("sharedtype.typescript.output-file-name"))
                .interfacePropertyDelimiter(properties.getProperty("sharedtype.typescript.interface-property-delimiter").charAt(0))
                .javaObjectMapType(properties.getProperty("sharedtype.typescript.java-object-map-type"))
                .optionalFieldFormats(parseEnumSet(properties,"sharedtype.typescript.optional-field-format",
                    Props.Typescript.OptionalFieldFormat.class, Props.Typescript.OptionalFieldFormat::fromString))
                .build())
            .rust(Props.Rust.builder()
                .outputFileName(properties.getProperty("sharedtype.rust.output-file-name"))
                .allowDeadcode(parseBoolean(properties, "sharedtype.rust.allow-deadcode"))
                .convertToSnakeCase(parseBoolean(properties, "sharedtype.rust.convert-to-snake-case"))
                .defaultTypeMacros(splitArray(properties.getProperty("sharedtype.rust.default-macros-traits")))
                .build())
            .build();
    }

    private static <T extends Enum<T>> Set<T> parseEnumSet(Properties properties, String propertyName, Class<T> type, Function<String, T> enumValueOf) {
        Set<String> trimmedElems = splitArray(properties.getProperty(propertyName));
        Set<T> set = EnumSet.noneOf(type);
        try {
            for (String trimmed : trimmedElems) {
                set.add(enumValueOf.apply(trimmed));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Failed to parse property '%s'", propertyName), e);
        }
        return set;
    }

    private static Set<String> splitArray(String value) {
        String[] arr = value.split(",");
        Set<String> trimmedElems = new LinkedHashSet<>(arr.length);
        for (String s : arr) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                trimmedElems.add(trimmed);
            }
        }
        return trimmedElems;
    }

    private static boolean parseBoolean(Properties properties, String key) {
        String value = properties.getProperty(key);
        if ("true".equals(value)) {
            return true;
        }
        if ("false".equals(value)) {
            return false;
        }
        throw new SharedTypeException(String.format("property '%s', can only be 'true' or 'false'.", key));
    }

    private static <T> Set<Class<? extends T>> parseClassSet(Properties properties, String propertyName) {
        String value = properties.getProperty(propertyName);
        Set<String> propertyValues = splitArray(value);
        Set<Class<? extends T>> annotations = new LinkedHashSet<>(propertyValues.size());
        for (String propertyValue : propertyValues) {
            try {
                annotations.add(parseClass(propertyValue));
            } catch (ClassNotFoundException e) {
                throw new SharedTypeException(String.format("Invalid property %s=%s", propertyName, value), e);
            }
        }
        return annotations;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> parseClass(String className) throws ClassNotFoundException {
        return (Class<? extends T>) Class.forName(className);
    }
}

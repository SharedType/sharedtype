package online.sharedtype.processor.context;

import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            properties.putAll(System.getProperties());
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
        Set<OutputTarget> targets = parseEnumSet(properties, "sharedtype.targets", OutputTarget.class, OutputTarget::valueOf);
        return Props.builder()
            .targets(targets)
            .targetTypes(targets.stream().map(OutputTarget::getTargetType).filter(Objects::nonNull).collect(Collectors.toSet()))
            .optionalAnnotations(splitArray(properties.getProperty("sharedtype.optional-annotations")))
            .ignoreAnnotations(splitArray(properties.getProperty("sharedtype.ignore-annotations")))
            .accessorAnnotations(splitArray(properties.getProperty("sharedtype.accessor-annotations")))
            .enumValueAnnotations(splitArray(properties.getProperty("sharedtype.enum-value-annotations")))
            .optionalContainerTypes(splitArray(properties.getProperty("sharedtype.optional-container-types")))
            .accessorGetterPrefixes(splitArray(properties.getProperty("sharedtype.accessor.getter-prefixes")))
            .arraylikeTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.array-like-types")))
            .maplikeTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.map-like-types")))
            .datetimelikeTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.datetime-like-types")))
            .ignoredTypeQualifiedNames(splitArray(properties.getProperty("sharedtype.ignored-types")))
            .ignoredFieldNames(splitArray(properties.getProperty("sharedtype.ignored-fields")))
            .constantNamespaced(parseBoolean(properties, "sharedtype.constant-namespaced"))
            .typescript(Props.Typescript.builder()
                .outputFileName(properties.getProperty("sharedtype.typescript.output-file-name"))
                .interfacePropertyDelimiter(properties.getProperty("sharedtype.typescript.interface-property-delimiter").charAt(0))
                .javaObjectMapType(properties.getProperty("sharedtype.typescript.java-object-map-type"))
                .targetDatetimeTypeLiteral(properties.getProperty("sharedtype.typescript.target-datetime-type"))
                .optionalFieldFormats(parseEnumSet(properties, "sharedtype.typescript.optional-field-format",
                    Props.Typescript.OptionalFieldFormat.class, Props.Typescript.OptionalFieldFormat::fromString))
                .enumFormat(parseEnum(properties, "sharedtype.typescript.enum-format", Props.Typescript.EnumFormat::fromString))
                .fieldReadonlyType(parseEnum(properties, "sharedtype.typescript.field-readonly-type", Props.Typescript.FieldReadonlyType::fromString))
                .typeMappings(parseMap(properties, "sharedtype.typescript.type-mappings"))
                .customCodePath(properties.getProperty("sharedtype.typescript.custom-code-path"))
                .build())
            .go(Props.Go.builder()
                .outputFileName(properties.getProperty("sharedtype.go.output-file-name"))
                .outputFilePackageName(properties.getProperty("sharedtype.go.output-file-package-name"))
                .javaObjectMapType(properties.getProperty("sharedtype.go.java-object-map-type"))
                .targetDatetimeTypeLiteral(properties.getProperty("sharedtype.go.target-datetime-type"))
                .enumFormat(parseEnum(properties, "sharedtype.go.enum-format", Props.Go.EnumFormat::fromString))
                .typeMappings(parseMap(properties, "sharedtype.go.type-mappings"))
                .customCodePath(properties.getProperty("sharedtype.go.custom-code-path"))
                .build())
            .rust(Props.Rust.builder()
                .outputFileName(properties.getProperty("sharedtype.rust.output-file-name"))
                .allowDeadcode(parseBoolean(properties, "sharedtype.rust.allow-deadcode"))
                .convertToSnakeCase(parseBoolean(properties, "sharedtype.rust.convert-to-snake-case"))
                .defaultTypeMacros(splitArray(properties.getProperty("sharedtype.rust.default-macros-traits")))
                .targetDatetimeTypeLiteral(properties.getProperty("sharedtype.rust.target-datetime-type"))
                .typeMappings(parseMap(properties, "sharedtype.rust.type-mappings"))
                .customCodePath(properties.getProperty("sharedtype.rust.custom-code-path"))
                .build())
            .build();
    }

    private static <T extends Enum<T>> Set<T> parseEnumSet(Properties properties, String propertyName, Class<T> type, Function<String, T> enumValueOf) {
        Set<String> trimmedElems = splitArray(properties.getProperty(propertyName));
        try {
            return EnumParsingUtils.parseEnumSet(trimmedElems, type, enumValueOf);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Failed to parse property '%s'", propertyName), e);
        }
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
        throw new IllegalArgumentException(String.format("property '%s', can only be 'true' or 'false', but it is '%s'.", key, value));
    }

    private static <T> Set<Class<? extends T>> parseClassSet(Properties properties, String propertyName) {
        String value = properties.getProperty(propertyName);
        Set<String> propertyValues = splitArray(value);
        Set<Class<? extends T>> annotations = new LinkedHashSet<>(propertyValues.size());
        for (String propertyValue : propertyValues) {
            try {
                annotations.add(parseClass(propertyValue));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(String.format("Invalid property %s=%s", propertyName, value), e);
            }
        }
        return annotations;
    }

    private static <T extends Enum<T>> T parseEnum(Properties properties, String propertyName, Function<String, T> fromString) {
        String value = properties.getProperty(propertyName);
        try {
            return fromString.apply(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Invalid property %s=%s", propertyName, value), e);
        }
    }

    private static Map<String, String> parseMap(Properties properties, String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value == null || value.isEmpty()) {
            return Collections.emptyMap();
        }
        String[] pairs = value.split(",");
        Map<String, String> map = new HashMap<>(pairs.length);
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split(":");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException(String.format("Invalid property %s=%s, entries must be separated by commas and key-value by colons.", propertyName, value));
            }
            map.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> parseClass(String className) throws ClassNotFoundException {
        return (Class<? extends T>) Class.forName(className);
    }
}

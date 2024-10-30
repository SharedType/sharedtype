package org.sharedtype.processor.context;

import org.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
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
            return loadProps(properties);
        } catch (Exception e) {
            throw new SharedTypeException("Failed to load properties.", e);
        }
    }

    private static Props loadProps(Properties properties) throws Exception {
        return Props.builder()
            .targets(parseSet(properties.getProperty("sharedtype.targets"), OutputTarget.class))
            .optionalAnno(parseAnnotationClass(properties.getProperty("sharedtype.optional-annotations")))
            .accessorGetterPrefixes(parseSet(properties.getProperty("sharedtype.accessor.getter-prefixes")))
            .arraylikeTypeQualifiedNames(parseSet(properties.getProperty("sharedtype.array-like-types")))
            .maplikeTypeQualifiedNames(parseSet(properties.getProperty("sharedtype.map-like-types")))
            .ignoredTypeQualifiedNames(parseSet(properties.getProperty("sharedtype.ignored-types")))
            .typescript(Props.Typescript.builder()
                .outputFileName(properties.getProperty("sharedtype.typescript.output-file-name"))
                .interfacePropertyDelimiter(properties.getProperty("sharedtype.typescript.interface-property-delimiter").charAt(0))
                .javaObjectMapType(properties.getProperty("sharedtype.typescript.java-object-map-type"))
                .build())
            .build();
    }

    private static Set<String> parseSet(String value) {
        return parseSet(value, String.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Set<T> parseSet(String value, Class<T> type) {
        String[] arr = value.split(",");
        Set<T> set = new LinkedHashSet<>(arr.length);
        for (String s : arr) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                if (type.equals(String.class)) {
                    set.add((T) trimmed);
                } else if (type.isEnum()) {
                    set.add((T) Enum.valueOf((Class<? extends Enum>) type, trimmed));
                } else {
                    throw new UnsupportedOperationException(String.format("Unsupported type: %s", type));
                }
            }
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> parseAnnotationClass(String className) throws ClassNotFoundException {
        return (Class<? extends Annotation>) Class.forName(className);
    }
}

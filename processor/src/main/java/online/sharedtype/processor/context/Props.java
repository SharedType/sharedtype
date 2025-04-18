package online.sharedtype.processor.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

/**
 * Global properties.
 *
 * @author Cause Chung
 */
@Builder(access = AccessLevel.PACKAGE)
@Getter
public final class Props {
    private final Set<OutputTarget> targets;
    private final Typescript typescript;
    private final Rust rust;

    private final Set<String> optionalAnnotations;
    private final Set<String> ignoreAnnotations;
    private final Set<String> accessorAnnotations;
    private final Set<String> enumValueAnnotations;
    private final Set<String> optionalContainerTypes;
    private final Set<String> accessorGetterPrefixes;
    private final Set<String> arraylikeTypeQualifiedNames;
    private final Set<String> maplikeTypeQualifiedNames;
    private final Set<String> datetimelikeTypeQualifiedNames;
    private final Set<String> ignoredTypeQualifiedNames;
    private final Set<String> ignoredFieldNames;
    private final boolean constantNamespaced;

    @Builder(access = AccessLevel.PACKAGE)
    @Getter
    public static final class Typescript {
        private final String outputFileName;
        private final char interfacePropertyDelimiter;
        private final String javaObjectMapType;
        private final String targetDatetimeTypeLiteral;
        private final Set<OptionalFieldFormat> optionalFieldFormats;
        private final EnumFormat enumFormat;
        private final FieldReadonlyType fieldReadonlyType;
        private final Map<String, String> typeMappings;
        private final String customCodePath;

        @Getter
        public enum OptionalFieldFormat {
            QUESTION_MARK("?"),
            NULL("null"),
            UNDEFINED("undefined"),
            ;
            private final String value;
            OptionalFieldFormat(String value) {
                this.value = value;
            }
            public static OptionalFieldFormat fromString(String value) {
                for (OptionalFieldFormat format : OptionalFieldFormat.values()) {
                    if (format.value.equals(value)) {
                        return format;
                    }
                }
                throw new IllegalArgumentException(String.format("Unknown optional field format: '%s', only '?', 'null', 'undefined' are allowed", value));
            }
        }

        public enum EnumFormat {
            UNION, CONST_ENUM, ENUM,
            ;
            public static EnumFormat fromString(String value) {
                return EnumFormat.valueOf(value.toUpperCase());
            }
        }

        public enum FieldReadonlyType {
            ALL, ACYCLIC, NONE,
            ;
            public static FieldReadonlyType fromString(String value) {
                return FieldReadonlyType.valueOf(value.toUpperCase());
            }
        }
    }

    @Builder(access = AccessLevel.PACKAGE)
    @Getter
    public static final class Rust {
        private final String outputFileName;
        private final boolean allowDeadcode;
        private final boolean convertToSnakeCase;
        private final Set<String> defaultTypeMacros;
        private final String targetDatetimeTypeLiteral;
        private final Map<String, String> typeMappings;
        private final String customCodePath;
    }
}

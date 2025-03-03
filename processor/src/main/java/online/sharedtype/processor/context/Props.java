package online.sharedtype.processor.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
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

    private final Set<Class<? extends Annotation>> optionalAnnotations;
    private final Set<String> optionalContainerTypes;
    private final Set<String> accessorGetterPrefixes;
    private final Set<String> arraylikeTypeQualifiedNames;
    private final Set<String> maplikeTypeQualifiedNames;
    private final Set<String> ignoredTypeQualifiedNames;
    private final Set<String> ignoredFieldNames;
    private final boolean constantNamespaced;

    @Builder(access = AccessLevel.PACKAGE)
    @Getter
    public static final class Typescript {
        private final String outputFileName;
        private final char interfacePropertyDelimiter;
        private final String javaObjectMapType;
    }

    @Builder(access = AccessLevel.PACKAGE)
    @Getter
    public static final class Rust {
        private final String outputFileName;
        private final boolean allowDeadcode;
        private final boolean convertToSnakeCase;
        private final Set<String> defaultTypeMacros;
    }
}

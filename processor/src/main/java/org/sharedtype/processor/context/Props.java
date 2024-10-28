package org.sharedtype.processor.context;

import lombok.Getter;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

@Getter
public final class Props {
    private static final Class<? extends Annotation> DEFAULT_OPTIONAL_ANNO = javax.annotation.Nullable.class;

    private static final Set<Language> DEFAULT_EMITTED_LANGUAGES = Set.of(Language.TYPESCRIPT);
    private static final String DEFAULT_TYPESCRIPT_OUTPUT_FILE_NAME = "types.d.ts";

    private final Set<Language> emittedLanguages = DEFAULT_EMITTED_LANGUAGES;
    private final String typescriptOutputFileName = DEFAULT_TYPESCRIPT_OUTPUT_FILE_NAME;

    private final boolean consoleWriterEnabled = true;
    private final boolean javaSerializationFileWriterEnabled = true;

    private final Class<? extends Annotation> optionalAnno = DEFAULT_OPTIONAL_ANNO;
    private final String javaObjectMapType = "any";
    private final Set<String> accessorGetterPrefixes = Set.of("get", "is");
    private final Set<String> arraylikeTypeQualifiedNames = Set.of(
        Iterable.class.getName()
    );
    private final Set<String> maplikeTypeQualifiedNames = Set.of(
        Map.class.getName()
    );
    private final Set<String> ignoredTypeQualifiedNames = Set.of(
        Object.class.getName(),
        Record.class.getName(),
        Serializable.class.getName(),
        Enum.class.getName()
    );
}

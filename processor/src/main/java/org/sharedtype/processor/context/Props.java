package org.sharedtype.processor.context;

import lombok.Getter;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

@Getter
public final class Props {
    private final Set<Language> emittedLanguages = Set.of(Language.TYPESCRIPT);
    private final Typescript typescript = new Typescript();

    private final boolean consoleWriterEnabled = true;
    private final boolean javaSerializationFileWriterEnabled = true;

    private final Class<? extends Annotation> optionalAnno = javax.annotation.Nullable.class;
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

    @Getter
    public static final class Typescript {
        private final String outputFileName = "types.d.ts";
        private final char interfacePropertyDelimiter = ';';
    }
}

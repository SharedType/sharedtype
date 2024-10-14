package org.sharedtype.processor.context;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.Set;

@Getter
public final class Props {
    private static final Class<? extends Annotation> DEFAULT_OPTIONAL_ANNO = javax.annotation.Nullable.class;

    private final Class<? extends Annotation> optionalAnno = DEFAULT_OPTIONAL_ANNO;
    private final String javaObjectMapType = "any";
    private final Set<String> arraylikeTypeQualifiedNames = Set.of(
            Iterable.class.getName()
    );
}

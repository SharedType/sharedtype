package org.jets.processor.context;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

import lombok.Getter;

@Getter
public final class JetsProps {
    private static final Class<? extends Annotation> DEFAULT_OPTIONAL_ANNO = javax.annotation.Nullable.class;

    private final Class<? extends Annotation> optionalAnno = DEFAULT_OPTIONAL_ANNO;
    private final String javaObjectMapType = "any";
    private final Set<String> arraylikeTypeQualifiedNames = Set.of(
            Collection.class.getName()
    );
}

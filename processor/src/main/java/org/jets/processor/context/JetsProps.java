package org.jets.processor.context;

import java.lang.annotation.Annotation;

import lombok.Getter;

@Getter
public final class JetsProps {
    static final Class<? extends Annotation> DEFAULT_OPTIONAL_ANNO = javax.annotation.Nullable.class;

    private final Class<? extends Annotation> optionalAnno = DEFAULT_OPTIONAL_ANNO;
    private final String javaObjectMapType = "any";
}

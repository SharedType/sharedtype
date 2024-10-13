package org.jets.processor.context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.stream.Collectors;

public final class ExtraUtils {
    private final Types types;
    private final Elements elements;
    private final Set<TypeMirror> toArrayTypes;

    ExtraUtils(ProcessingEnvironment processingEnv, JetsProps props) {
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        toArrayTypes = props.getToArrayTypeQualifiedNames().stream()
                .map(qualifiedName -> types.erasure(elements.getTypeElement(qualifiedName).asType()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean typeShouldBeTreatedAsArray(TypeMirror typeMirror) {
        for (TypeMirror toArrayType : toArrayTypes) {
            if (types.isSubtype(types.erasure(typeMirror), toArrayType)) {
                return true;
            }
        }
        return false;
    }
}

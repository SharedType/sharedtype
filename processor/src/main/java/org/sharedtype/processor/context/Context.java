package org.sharedtype.processor.context;

import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Collectors;

public final class Context {
    private final TypeCache resolvedTypes = new TypeCache();
    @Getter
    private final ProcessingEnvironment processingEnv;
    @Getter
    private final Props props;
    private final Types types;
    private final Elements elements;
    private final Set<TypeMirror> arraylikeTypes;

    public Context(ProcessingEnvironment processingEnv, Props props) {
        this.processingEnv = processingEnv;
        this.props = props;
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        arraylikeTypes = props.getArraylikeTypeQualifiedNames().stream()
                .map(qualifiedName -> types.erasure(elements.getTypeElement(qualifiedName).asType()))
                .collect(Collectors.toUnmodifiableSet());
    }

    // TODO: optimize by remove varargs
    public void info(String message, Object... objects) {
        log(Diagnostic.Kind.NOTE, message, objects);
    }

    public void error(String message, Object... objects) {
        log(Diagnostic.Kind.ERROR, message, objects);
    }

    public void saveType(String qualifiedName, String name) {
        resolvedTypes.add(qualifiedName, name);
    }

    public boolean hasType(String qualifiedName) {
        return resolvedTypes.contains(qualifiedName);
    }

    /**
     * Should check if the type is saved to the context by calling {@link #hasType(String)} first.
     * @return the simple name of the type, null if not saved to the context.
     */
    public String getSimpleName(String qualifiedName) {
        return resolvedTypes.getName(qualifiedName);
    }

    public boolean isArraylike(TypeMirror typeMirror) {
        for (TypeMirror toArrayType : arraylikeTypes) {
            if (types.isSubtype(types.erasure(typeMirror), toArrayType)) {
                return true;
            }
        }
        return false;
    }

    private void log(Diagnostic.Kind level, String message, Object... objects) {
        processingEnv.getMessager().printMessage(level, String.format("[ST] %s", String.format(message, objects)));
    }
}

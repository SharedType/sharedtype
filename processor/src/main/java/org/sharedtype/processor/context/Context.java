package org.sharedtype.processor.context;

import lombok.Getter;
import lombok.Setter;
import org.sharedtype.processor.parser.TypeDefParser;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Collectors;

public final class Context {
    @Getter
    private final TypeCache typeCache = new TypeCache();
    @Getter
    private final ProcessingEnvironment processingEnv;
    @Getter
    private final Props props;
    private final Types types;
    private final Elements elements;
    private final Set<TypeMirror> arraylikeTypes;
    @Getter @Setter
    private TypeDefParser typeDefParser;

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
    public void warning(String message, Object... objects) {
        log(Diagnostic.Kind.WARNING, message, objects);
    }
    public void error(String message, Object... objects) {
        log(Diagnostic.Kind.ERROR, message, objects);
    }

    public boolean isArraylike(TypeMirror typeMirror) {
        for (TypeMirror toArrayType : arraylikeTypes) {
            if (types.isSubtype(types.erasure(typeMirror), toArrayType)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTypeIgnored(TypeElement typeElement) {
        return props.getIgnoredTypeQualifiedNames().contains(typeElement.getQualifiedName().toString());
    }

    private void log(Diagnostic.Kind level, String message, Object... objects) {
        processingEnv.getMessager().printMessage(level, String.format("[ST] %s", String.format(message, objects)));
    }
}

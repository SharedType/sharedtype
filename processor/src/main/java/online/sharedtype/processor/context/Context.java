package online.sharedtype.processor.context;

import com.sun.source.util.Trees;
import lombok.Getter;
import online.sharedtype.SharedType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Annotation processing context state and utils.
 *
 * @author Cause Chung
 */
public final class Context {
    @Getter
    private final TypeStore typeStore = new TypeStore();
    @Getter
    private final RenderFlags renderFlags = new RenderFlags();
    @Getter
    private final ProcessingEnvironment processingEnv;
    @Getter
    private final Props props;
    private final Types types;
    private final Elements elements;
    @Getter
    private final Trees trees;
    private final Set<TypeMirror> arraylikeTypes;
    private final Set<TypeMirror> maplikeTypes;

    public Context(ProcessingEnvironment processingEnv, Props props) {
        this.processingEnv = processingEnv;
        this.props = props;
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        trees = Trees.instance(processingEnv);
        arraylikeTypes = props.getArraylikeTypeQualifiedNames().stream()
                .map(qualifiedName -> types.erasure(elements.getTypeElement(qualifiedName).asType()))
                .collect(Collectors.toSet());
        maplikeTypes = props.getMaplikeTypeQualifiedNames().stream()
                .map(qualifiedName -> types.erasure(elements.getTypeElement(qualifiedName).asType()))
                .collect(Collectors.toSet());
    }

    // TODO: optimize by remove varargs
    public void info(String message, Object... objects) {
        log(Diagnostic.Kind.NOTE, message, objects);
    }
    public void warn(String message, Object... objects) {
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

    public boolean isMaplike(TypeMirror typeMirror) {
        for (TypeMirror maplikeType : maplikeTypes) {
            if (types.isSubtype(types.erasure(typeMirror), maplikeType)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnumType(TypeMirror typeMirror) {
        return types.asElement(typeMirror).getKind() == ElementKind.ENUM;
    }

    public boolean isIgnored(Element element) {
        if (element.getAnnotation(SharedType.Ignore.class) != null) {
            return true;
        }
        if (element.getKind() == ElementKind.FIELD) {
            return props.getIgnoredFieldNames().contains(element.getSimpleName().toString());
        } else if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            return props.getIgnoredTypeQualifiedNames().contains(typeElement.getQualifiedName().toString());
        }
        return false;
    }

    public boolean isOptionalType(String qualifiedName) {
        return props.getOptionalContainerTypes().contains(qualifiedName);
    }

    public FileObject createSourceOutput(String filename) throws IOException {
        return processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", filename);
    }

    private void log(Diagnostic.Kind level, String message, Object... objects) {
        processingEnv.getMessager().printMessage(level, String.format("[ST] %s", String.format(message, objects)));
    }
}

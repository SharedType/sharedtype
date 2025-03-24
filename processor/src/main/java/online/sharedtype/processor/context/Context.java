package online.sharedtype.processor.context;

import com.sun.source.util.Trees;
import lombok.Getter;
import online.sharedtype.SharedType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

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
    @Getter
    private final Trees trees;

    public Context(ProcessingEnvironment processingEnv, Props props) {
        this.processingEnv = processingEnv;
        this.props = props;
        types = processingEnv.getTypeUtils();
        trees = Trees.instance(processingEnv);
    }

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
        return isSubtypeOfAny(typeMirror, props.getArraylikeTypeQualifiedNames());
    }

    /** Check if the type is directly the same type as one of the defined arraylike types */
    public boolean isTopArrayType(TypeMirror typeMirror) {
        for (String qualifiedName : props.getArraylikeTypeQualifiedNames()) {
            if (isSameTypeOf(typeMirror, qualifiedName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMaplike(TypeMirror typeMirror) {
        return isSubtypeOfAny(typeMirror, props.getMaplikeTypeQualifiedNames());
    }

    public boolean isDatetimelike(TypeMirror typeMirror) {
        return isSubtypeOfAny(typeMirror, props.getDatetimelikeTypeQualifiedNames());
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

    private boolean isSubtypeOfAny(TypeMirror typeMirror, Set<String> qualifiedNames) {
        for (String qualifiedName : qualifiedNames) {
            if (isSubtypeOf(typeMirror, qualifiedName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSubtypeOf(TypeMirror typeMirror, String qualifiedName) {
        Queue<TypeMirror> queue = new ArrayDeque<>();
        queue.add(typeMirror);
        Set<TypeMirror> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            TypeMirror type = queue.poll();
            if(isSameTypeOf(type, qualifiedName)) {
                return true;
            }
            for (TypeMirror directSupertype : types.directSupertypes(type)) {
                if(!visited.contains(directSupertype) && !props.getIgnoredTypeQualifiedNames().contains(directSupertype.toString())) {
                    queue.add(directSupertype);
                    visited.add(directSupertype);
                }
            }
        }
        return false;
    }

    private static boolean isSameTypeOf(TypeMirror typeMirror, String qualifiedName) {
        if (typeMirror instanceof DeclaredType) {
            Element element = ((DeclaredType) typeMirror).asElement();
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                return typeElement.getQualifiedName().contentEquals(qualifiedName);
            }
        }
        return false;
    }
}

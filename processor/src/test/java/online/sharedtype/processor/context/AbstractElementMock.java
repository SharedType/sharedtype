package online.sharedtype.processor.context;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class AbstractElementMock<E extends Element, T extends TypeMirror, M extends AbstractElementMock<E, T, M>> {
    final E element;
    final T type;
    final Context ctx;
    final Types types;
    final Elements elements;

    AbstractElementMock(E element, T type, Context ctx) {
        this.element = element;
        this.type = type;
        this.ctx = ctx;
        this.types = ctx.getProcessingEnv().getTypeUtils();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        when(element.asType()).thenReturn(type);
    }

    public final M withElementKind(ElementKind elementKind) {
        when(element.getKind()).thenReturn(elementKind);
        return returnThis();
    }

    public final M withTypeArguments(TypeMirror... typeArgsArr) {
        List<? extends TypeMirror> typeArgs = Arrays.asList(typeArgsArr);
        if (type instanceof DeclaredType declaredType) {
            when(declaredType.getTypeArguments()).thenAnswer(invoc -> typeArgs);
        } else {
            fail("Not a DeclaredType: " + type);
        }
        return returnThis();
    }

    public final <A extends Annotation> M withAnnotation(Class<A> annotationClazz) {
        Consumer<A> mockCustomizer = anno -> {};
        return withAnnotation(annotationClazz, mockCustomizer);
    }
    public final <A extends Annotation> M withAnnotation(Class<A> annotationClazz, Consumer<A> mockCustomizer) {
        A annotation = mock(annotationClazz, RETURNS_SMART_NULLS);
        mockCustomizer.accept(annotation);
        return withAnnotation(annotationClazz, () -> annotation);
    }
    public final <A extends Annotation> M withAnnotation(Class<A> annotationClazz, Supplier<A> supplier) {
        when(element.getAnnotation(annotationClazz)).thenReturn(supplier.get());
        var mockAnnotationMirror = mock(AnnotationMirror.class);
        when(element.getAnnotationMirrors()).thenAnswer(invoc -> List.of(mockAnnotationMirror));
        var mockAnnotationType = mock(DeclaredType.class);
        when(mockAnnotationMirror.getAnnotationType()).thenAnswer(invoc -> mockAnnotationType);
        when(mockAnnotationType.toString()).thenReturn(annotationClazz.getCanonicalName());

        var existingAnnotations = element.getAnnotationsByType(annotationClazz);
        int arrLength = existingAnnotations != null ? existingAnnotations.length + 1 : 1;
        A[] arr = createArray(annotationClazz, arrLength);
        arr[arrLength - 1] = supplier.get();
        if (existingAnnotations != null) {
            System.arraycopy(existingAnnotations, 0, arr, 0, existingAnnotations.length);
        }
        when(element.getAnnotationsByType(annotationClazz)).thenAnswer(invoc -> arr);
        return returnThis();
    }

    public final M withAnnotationMirrors(AnnotationMirror... annotationMirrors) {
        when(element.getAnnotationMirrors()).thenAnswer(invoc -> Arrays.asList(annotationMirrors));
        return returnThis();
    }

    public final M withModifiers(Modifier... modifiers) {
        when(element.getModifiers()).thenReturn(Set.of(modifiers));
        return returnThis();
    }

    public M withSuperTypes(TypeMirror... superTypes) {
        when(types.directSupertypes(type)).thenAnswer(invoc -> Arrays.asList(superTypes));
        return returnThis();
    }

    public M withEnclosingElement(Element enclosingElement) {
        when(element.getEnclosingElement()).thenReturn(enclosingElement);
        var existingEnclosedElements = enclosingElement.getEnclosedElements();
        List<Element> newEnclosedElements = new ArrayList<>(existingEnclosedElements.size() + 1);
        newEnclosedElements.addAll(existingEnclosedElements);
        newEnclosedElements.add(element);
        when(enclosingElement.getEnclosedElements()).thenAnswer(invoc -> newEnclosedElements);
        return returnThis();
    }

    public M withPackageElement(PackageElement packageElement) {
        when(elements.getPackageOf(element)).thenReturn(packageElement);
        return returnThis();
    }

    public final M ofTree(VariableTreeMock tree) {
        tree.fromElement(element);
        return returnThis();
    }

    public final E element() {
        return element;
    }

    public final T type() {
        return type;
    }

    static void setQualifiedName(TypeElement typeElement, String qualifiedName) {
        when(typeElement.getQualifiedName()).thenReturn(new MockName(qualifiedName));
    }

    static void setQualifiedName(PackageElement packageElement, String qualifiedName) {
        when(packageElement.getQualifiedName()).thenReturn(new MockName(qualifiedName));
    }

    static void setSimpleName(Element element, String simpleName) {
        when(element.getSimpleName()).thenReturn(new MockName(simpleName));
    }

    @SuppressWarnings("unchecked")
    private M returnThis() {
        return (M)this;
    }

    @SuppressWarnings("unchecked")
    private static <A> A[] createArray(Class<A> clazz, int length) {
        return (A[]) Array.newInstance(clazz, length);
    }
}

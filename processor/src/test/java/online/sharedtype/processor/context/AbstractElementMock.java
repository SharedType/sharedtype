package online.sharedtype.processor.context;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import java.lang.annotation.Annotation;
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

    AbstractElementMock(E element, T type, Context ctx, Types types) {
        this.element = element;
        this.type = type;
        this.ctx = ctx;
        this.types = types;
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
        return returnThis();
    }

    public final M withModifiers(Modifier... modifiers) {
        when(element.getModifiers()).thenReturn(Set.of(modifiers));
        return returnThis();
    }

    public final E element() {
        return element;
    }

    public final T type() {
        return type;
    }

    static void setQualifiedName(TypeElement typeElement, String qualifiedName) {
        Name typeElementName = mock(Name.class);
        when(typeElement.getQualifiedName()).thenReturn(typeElementName);
        when(typeElementName.toString()).thenReturn(qualifiedName);
    }

    static void setSimpleName(Element element, String simpleName) {
        Name elementName = mock(Name.class);
        when(element.getSimpleName()).thenReturn(elementName);
        when(elementName.toString()).thenReturn(simpleName);
    }

    @SuppressWarnings("unchecked")
    private M returnThis() {
        return (M)this;
    }
}

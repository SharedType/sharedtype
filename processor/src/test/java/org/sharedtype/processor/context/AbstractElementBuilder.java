package org.sharedtype.processor.context;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class AbstractElementBuilder<E extends Element, T extends TypeMirror, B extends AbstractElementBuilder<E, T, B> > {
    final E element;
    final T type;
    final Context ctx;
    final Types types;

    AbstractElementBuilder(E element, T type, Context ctx, Types types) {
        this.element = element;
        this.type = type;
        this.ctx = ctx;
        this.types = types;
        when(element.asType()).thenReturn(type);
    }

    public E element() {
        return element;
    }

    public T type() {
        return type;
    }

    static void setQualifiedName(TypeElement typeElement, String qualifiedName) {
        var typeElementName = mock(Name.class);
        when(typeElement.getQualifiedName()).thenReturn(typeElementName);
        when(typeElementName.toString()).thenReturn(qualifiedName);
    }
}

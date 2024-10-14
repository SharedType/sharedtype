package org.sharedtype.processor.context;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class VariableElementBuilder<T extends TypeMirror> extends AbstractElementBuilder<VariableElement, T, VariableElementBuilder<T>> {
    VariableElementBuilder(Class<T> typeClazz, Context ctx, Types types) {
        super(mock(VariableElement.class), mock(typeClazz), ctx, types);
    }

    public VariableElementBuilder<T> withTypeKind(TypeKind typeKind) {
        when(type.getKind()).thenReturn(typeKind);
        return this;
    }

    public VariableElementBuilder<T> withTypeElement(TypeElement typeElement) {
        if (type instanceof DeclaredType declaredType) {
            when((declaredType).asElement()).thenReturn(typeElement);
        }
        return this;
    }
}

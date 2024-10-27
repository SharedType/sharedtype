package org.sharedtype.processor.context;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DeclaredTypeVariableElementMock extends AbstractElementMock<VariableElement, DeclaredType, DeclaredTypeVariableElementMock> {
    DeclaredTypeVariableElementMock(String name, DeclaredType declaredType, Context ctx, Types types) {
        super(mock(VariableElement.class, name), declaredType, ctx, types);
        setSimpleName(element, name);
    }

    public DeclaredTypeVariableElementMock withTypeKind(TypeKind typeKind) {
        when(type.getKind()).thenReturn(typeKind);
        return this;
    }

    public DeclaredTypeVariableElementMock ofTree(VariableTreeMock tree) {
        tree.fromElement(element);
        return this;
    }
}

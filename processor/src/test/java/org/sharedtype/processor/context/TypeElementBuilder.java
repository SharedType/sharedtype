package org.sharedtype.processor.context;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TypeElementBuilder extends AbstractElementBuilder<TypeElement, DeclaredType, TypeElementBuilder> {
    TypeElementBuilder(String qualifiedName, Context ctx, Types types) {
        super(mock(TypeElement.class, qualifiedName), mock(DeclaredType.class, qualifiedName), ctx, types);
        setQualifiedName(element, qualifiedName);
        when(type.getKind()).thenReturn(TypeKind.DECLARED);
        when(type.asElement()).thenReturn(element);
        when(types.asElement(type)).thenReturn(element);
    }
}

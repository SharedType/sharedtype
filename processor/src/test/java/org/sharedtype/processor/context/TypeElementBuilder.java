package org.sharedtype.processor.context;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TypeElementBuilder extends AbstractElementBuilder<TypeElement, DeclaredType, TypeElementBuilder> {
    TypeElementBuilder(String qualifiedName, Context ctx, Types types) {
        super(mock(TypeElement.class), mock(DeclaredType.class), ctx, types);
        setQualifiedName(element, qualifiedName);
        when(type.getKind()).thenReturn(TypeKind.DECLARED);
    }

    public TypeElementBuilder withTypeArguments(TypeElement... typeArgElements) {
        var typeArgs = Arrays.stream(typeArgElements).map(e -> (DeclaredType)e.asType()).toList();
        when(type.getTypeArguments()).then(invoc -> typeArgs);
        when(ctx.getTypeArguments(type)).thenReturn(typeArgs);
        return this;
    }
}

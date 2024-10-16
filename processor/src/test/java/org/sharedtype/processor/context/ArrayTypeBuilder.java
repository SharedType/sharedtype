package org.sharedtype.processor.context;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ArrayTypeBuilder {
    private final ArrayType type;

    ArrayTypeBuilder(TypeMirror componentType) {
        type = mock(ArrayType.class);
        when(type.getComponentType()).thenReturn(componentType);
        when(type.getKind()).thenReturn(TypeKind.ARRAY);
    }

    public ArrayType type() {
        return type;
    }
}

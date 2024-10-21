package org.sharedtype.processor.context;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class PrimitiveVariableElementMock extends AbstractElementMock<VariableElement, PrimitiveType, PrimitiveVariableElementMock> {
    PrimitiveVariableElementMock(String name, TypeKind typeKind, Context ctx, Types types) {
        super(mock(VariableElement.class, name), mock(PrimitiveType.class, typeKind.name()), ctx, types);
        assertThat(typeKind.isPrimitive()).isTrue();
        setSimpleName(element, name);
        when(element.getKind()).thenReturn(ElementKind.FIELD);
        when(type.getKind()).thenReturn(typeKind);
    }
}

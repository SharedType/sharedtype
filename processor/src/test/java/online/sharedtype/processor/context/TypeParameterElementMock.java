package online.sharedtype.processor.context;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TypeParameterElementMock extends AbstractElementMock<TypeParameterElement, TypeVariable, TypeParameterElementMock> {
    TypeParameterElementMock(String name, Context ctx, Types types) {
        super(mock(TypeParameterElement.class), mock(TypeVariable.class), ctx, types);
        when(type.getKind()).thenReturn(TypeKind.TYPEVAR);
        when(type.asElement()).thenReturn(element);
        when(types.asElement(type)).thenReturn(element);
        setSimpleName(element, name);
    }
}

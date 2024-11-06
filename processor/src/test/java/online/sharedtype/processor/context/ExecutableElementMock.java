package online.sharedtype.processor.context;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ExecutableElementMock extends AbstractElementMock<ExecutableElement, ExecutableType, ExecutableElementMock> {
    private static final ElementKind DEFAULT_ELEMENT_KIND = ElementKind.METHOD;

    ExecutableElementMock(String name, Context ctx, Types types) {
        super(mock(ExecutableElement.class, name), mock(ExecutableType.class, name), ctx, types);
        setSimpleName(element, name);
        when(type.getKind()).thenReturn(TypeKind.EXECUTABLE);
        when(element.getKind()).thenReturn(DEFAULT_ELEMENT_KIND);
    }

    public ExecutableElementMock withReturnType(TypeMirror returnType) {
        when(element.getReturnType()).thenReturn(returnType);
        when(type.getReturnType()).thenReturn(returnType);
        return this;
    }

    public ExecutableElementMock withParameters(VariableElement... parameters) {
        when(element.getParameters()).then(invoc -> Arrays.asList(parameters));
        return this;
    }
}

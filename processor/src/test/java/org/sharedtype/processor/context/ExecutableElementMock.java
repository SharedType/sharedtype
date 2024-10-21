package org.sharedtype.processor.context;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;

public final class ExecutableElementMock extends AbstractElementMock<ExecutableElement, ExecutableType, ExecutableElementMock> {
    ExecutableElementMock(String name, Context ctx, Types types) {
        super(mock(ExecutableElement.class, name), mock(ExecutableType.class, name), ctx, types);
        setSimpleName(element, name);
    }
}

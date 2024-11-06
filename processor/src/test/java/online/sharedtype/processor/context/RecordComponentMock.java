package online.sharedtype.processor.context;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RecordComponentMock<T extends TypeMirror> extends AbstractElementMock<RecordComponentElement, T, RecordComponentMock<T>> {
    RecordComponentMock(String name, T type, Context ctx, Types types) {
        super(mock(RecordComponentElement.class, name), type, ctx, types);
        setSimpleName(element, name);
        when(element.asType()).thenReturn(type);
        when(element.getKind()).thenReturn(ElementKind.RECORD_COMPONENT);
    }

    public RecordComponentMock<T> withAccessor(ExecutableElement accessor) {
        when(element.getAccessor()).thenReturn(accessor);
        return this;
    }
}

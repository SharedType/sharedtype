package online.sharedtype.processor.context;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.DeclaredType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AnnotationMirrorMock {
    private final AnnotationMirror mocked = mock(AnnotationMirror.class);

    AnnotationMirrorMock(DeclaredType declaredType) {
        when(mocked.getAnnotationType()).thenReturn(declaredType);
    }

    public AnnotationMirror mocked() {
        return mocked;
    }
}

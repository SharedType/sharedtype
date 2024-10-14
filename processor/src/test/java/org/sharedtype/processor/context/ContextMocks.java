package org.sharedtype.processor.context;

import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Getter
public final class ContextMocks {
    private final TypeCache typeCache = new TypeCache();
    private final Props props;
    private final ProcessingEnvironment processingEnv = mock(ProcessingEnvironment.class);
    private final Types types = mock(Types.class);
    private final Elements elements = mock(Elements.class);
    private final Context context = mock(Context.class);

    public ContextMocks(Props props) {
        this.props = props;
        when(context.getProps()).thenReturn(props);
        when(context.getProcessingEnv()).thenReturn(processingEnv);
        when(processingEnv.getElementUtils()).thenReturn(elements);
        when(processingEnv.getTypeUtils()).thenReturn(types);
        doAnswer(invoc -> {
            String qualifiedName = invoc.getArgument(0);
            String simpleName = invoc.getArgument(1);
            typeCache.add(qualifiedName, simpleName);
            return null;
        }).when(context).saveType(anyString(), anyString());
        when(context.getSimpleName(anyString())).then(invoc -> typeCache.getName(invoc.getArgument(0)));
        when(context.hasType(anyString())).then(invoc -> typeCache.contains(invoc.getArgument(0)));
    }

    public ContextMocks() {
        this(new Props());
    }

    public TypeElementBuilder buildTypeElement(String qualifiedName) {
        return new TypeElementBuilder(qualifiedName, context, types);
    }

    public <T extends TypeMirror> VariableElementBuilder<T> buildVariableElement(Class<T> typeClazz) {
        return new VariableElementBuilder<>(typeClazz, context, types);
    }
}

package org.jets.processor.parser.context;

import lombok.Getter;
import org.jets.processor.context.Context;
import org.jets.processor.context.ExtraUtils;
import org.jets.processor.context.JetsProps;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Getter
public final class ContextMocks {
    private final JetsProps props;
    private final ProcessingEnvironment processingEnv = mock(ProcessingEnvironment.class);
    private final Context context = mock(Context.class);
    private final ExtraUtils extraUtils = mock(ExtraUtils.class);
    private final Types types = mock(Types.class);
    private final Elements elements = mock(Elements.class);

    public ContextMocks(JetsProps props) {
        this.props = props;
        when(context.getProps()).thenReturn(props);
        when(context.getExtraUtils()).thenReturn(extraUtils);
        when(context.getProcessingEnv()).thenReturn(processingEnv);
        when(processingEnv.getElementUtils()).thenReturn(elements);
        when(processingEnv.getTypeUtils()).thenReturn(types);
    }

    public ContextMocks() {
        this(new JetsProps());
    }
}

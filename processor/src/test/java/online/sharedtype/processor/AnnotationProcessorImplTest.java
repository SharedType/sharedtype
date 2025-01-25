package online.sharedtype.processor;

import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.context.ContextMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import online.sharedtype.processor.parser.TypeDefParser;
import online.sharedtype.processor.resolver.TypeResolver;
import online.sharedtype.processor.writer.TypeWriter;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnnotationProcessorImplTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeDefParser typeDefParser = mock(TypeDefParser.class);
    private final TypeResolver typeResolver = mock(TypeResolver.class);
    private final TypeWriter typeWriter = mock(TypeWriter.class);
    private final AnnotationProcessorImpl processor = new AnnotationProcessorImpl();

    private final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    @BeforeEach
    void setUp() {
        processor.ctx = ctxMocks.getContext();
        processor.parser = typeDefParser;
        processor.resolver = typeResolver;
        processor.writer = typeWriter;
    }

    @Test
    void doProcess() throws Exception {
        var typeElement1 = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
        var typeElement2 = ctxMocks.typeElement("com.github.cuzfrog.IgnoredClass").element();
        var classDef1 = ClassDef.builder().qualifiedName("com.github.cuzfrog.Abc").simpleName("Abc").build();
        when(typeDefParser.parse(typeElement1)).thenReturn(classDef1);
        when(typeDefParser.parse(typeElement2)).thenReturn(null);

        var dependencyDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Dependency").simpleName("Dependency").build();
        when(typeResolver.resolve(List.of(classDef1))).thenReturn(List.of(classDef1, dependencyDef));

        processor.doProcess(Set.of(typeElement1, typeElement2));

        verify(typeWriter).write(List.of(classDef1, dependencyDef));
        verify(ctxMocks.getContext()).warning(messageCaptor.capture(), eq("com.github.cuzfrog.IgnoredClass"), eq(Constants.ANNOTATION_QUALIFIED_NAME));
        assertThat(messageCaptor.getValue()).contains("is ignored or invalid");
    }
}

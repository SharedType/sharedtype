package online.sharedtype.processor.writer;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.writer.adaptor.RenderDataAdaptor;
import online.sharedtype.processor.writer.adaptor.RenderDataAdaptorFactory;
import online.sharedtype.processor.writer.converter.AbstractTypeExpr;
import online.sharedtype.processor.writer.converter.TemplateDataConverter;
import online.sharedtype.processor.writer.render.Template;
import online.sharedtype.processor.writer.render.TemplateRenderer;
import online.sharedtype.processor.support.utils.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.tools.FileObject;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class TemplateTypeFileWriterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private @Mock TemplateRenderer renderer;
    private @Mock RenderDataAdaptorFactory renderDataAdaptorFactory;
    private @Mock TemplateDataConverter converter1;
    private @Mock TemplateDataConverter converter2;
    private @Mock TemplateDataConverter converter3;
    private TemplateTypeFileWriter writer;

    private @Mock FileObject fileObject;
    private @Captor ArgumentCaptor<List<Tuple<Template, ?>>> renderDataCaptor;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

    @BeforeEach
    void setUp() throws Exception {
        writer = new TemplateTypeFileWriter(
            ctxMocks.getContext(), renderer, renderDataAdaptorFactory,
            Set.of(converter1, converter2, converter3), "types.ts");
        when(ctxMocks.getContext().createSourceOutput("types.ts")).thenReturn(fileObject);
        when(fileObject.openOutputStream()).thenReturn(outputStream);
    }

    @Test
    void writeToIOWriter() throws Exception {
        doAnswer(invoc -> {
            Writer writer = invoc.getArgument(0);
            writer.write("some-value");
            return null;
        }).when(renderer).render(any(), any());
        var renderDataAdaptor = mock(RenderDataAdaptor.class);
        when(renderDataAdaptorFactory.header(ctxMocks.getContext())).thenReturn(Tuple.of(Template.TEMPLATE_TYPESCRIPT_HEADER, renderDataAdaptor));

        ClassDef classDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.ClassA").build();
        var data1 = mock(AbstractTypeExpr.class);
        var data2 = mock(AbstractTypeExpr.class);
        when(converter1.shouldAccept(classDef)).thenReturn(true);
        when(converter2.shouldAccept(classDef)).thenReturn(true);
        when(converter3.shouldAccept(classDef)).thenReturn(false);
        when(converter1.convert(classDef)).thenReturn(Tuple.of(Template.TEMPLATE_TYPESCRIPT_INTERFACE, data1));
        when(converter2.convert(classDef)).thenReturn(Tuple.of(Template.TEMPLATE_RUST_STRUCT, data2));
        writer.write(List.of(classDef));

        verify(renderer).render(any(), renderDataCaptor.capture());

        List<Tuple<Template, ?>> renderData = renderDataCaptor.getValue();
        assertThat(renderData).satisfiesExactlyInAnyOrder(
            entry -> {
              assertThat(entry.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_HEADER);
              assertThat(entry.b()).isSameAs(renderDataAdaptor);
            },
            entry -> {
                assertThat(entry.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_INTERFACE);
                assertThat(entry.b()).isEqualTo(data1);
            },
            entry -> {
                assertThat(entry.a()).isEqualTo(Template.TEMPLATE_RUST_STRUCT);
                assertThat(entry.b()).isEqualTo(data2);
            }
        );
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void warnOnDuplicateSimpleName() throws Exception {
        writer.write(List.of(
            ClassDef.builder().qualifiedName("com.github.cuzfrog.ClassA").simpleName("ClassA").build(),
            ClassDef.builder().qualifiedName("com.github.cuzfrog.another.ClassA").simpleName("ClassA").build()
        ));

        verify(ctxMocks.getContext()).warn(any(), any(Object[].class));
        verify(renderer).render(any(), any());
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void constantNamespaceDefDoesNotCountDuplicateName() throws Exception {
        writer.write(List.of(
            ClassDef.builder().qualifiedName("com.github.cuzfrog.ClassA").simpleName("ClassA").build(),
            ConstantNamespaceDef.builder().qualifiedName("com.github.cuzfrog.ClassA").simpleName("ClassA").build()
        ));

        verify(ctxMocks.getContext(), never()).warn(any(), any(Object[].class));
        verify(renderer).render(any(), any());
    }
}

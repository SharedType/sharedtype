package org.sharedtype.processor.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.support.utils.Tuple;
import org.sharedtype.processor.writer.render.Template;
import org.sharedtype.processor.writer.render.TemplateRenderer;

import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sharedtype.domain.Constants.INT_TYPE_INFO;
import static org.sharedtype.domain.Constants.STRING_TYPE_INFO;

@ExtendWith(MockitoExtension.class)
final class TypescriptTypeFileWriterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private @Mock TemplateRenderer renderer;
    private TypescriptTypeFileWriter writer;

    private @Mock FileObject fileObject;
    private @Captor ArgumentCaptor<List<Tuple<Template, Object>>> renderDataCaptor;

    @BeforeEach
    void setUp() {
        writer = new TypescriptTypeFileWriter(ctxMocks.getContext(), renderer);
    }

    @Test
    void writeEnumUnion() throws IOException {
        when(ctxMocks.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "types.d.ts")).thenReturn(fileObject);

        var outputStream = new ByteArrayOutputStream(256);
        when(fileObject.openOutputStream()).thenReturn(outputStream);
        doAnswer(invoc -> {
            var writer = invoc.getArgument(0, Writer.class);
            writer.write("some-value");
            return null;
        }).when(renderer).render(any(), any());

        var enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(List.of(
                new EnumValueInfo(STRING_TYPE_INFO, "Value1"),
                new EnumValueInfo(INT_TYPE_INFO, 123)
            ))
            .build();
        when(ctxMocks.getElements().getConstantExpression("Value1")).thenReturn("\"Value1\"");
        when(ctxMocks.getElements().getConstantExpression(123)).thenReturn("123");

        writer.write(List.of(enumDef));

        verify(renderer).render(any(), renderDataCaptor.capture());

        assertThat(outputStream.toString()).isEqualTo("some-value");

        var data = renderDataCaptor.getValue();
        assertThat(data).hasSize(1);
        var model = (TypescriptTypeFileWriter.Model.EnumUnion) data.get(0).b();
        assertThat(model.name()).isEqualTo("EnumA");
        assertThat(model.values()).containsExactly("\"Value1\"", "123");

        when(ctxMocks.getElements().getConstantExpression(123)).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> writer.write(List.of(enumDef)))
            .hasMessageContaining("Failed to get constant expression for enum value: 123 of type int in enum");
    }
}

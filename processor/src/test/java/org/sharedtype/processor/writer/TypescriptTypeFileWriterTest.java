package org.sharedtype.processor.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sharedtype.domain.ArrayTypeInfo;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.FieldComponentInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.support.utils.Tuple;
import org.sharedtype.processor.writer.render.Template;
import org.sharedtype.processor.writer.render.TemplateRenderer;

import javax.tools.FileObject;
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

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

    @BeforeEach
    void setUp() throws IOException {
        writer = new TypescriptTypeFileWriter(ctxMocks.getContext(), renderer);
        when(ctxMocks.getContext().createSourceOutput("types.d.ts")).thenReturn(fileObject);
        when(fileObject.openOutputStream()).thenReturn(outputStream);
    }

    @Test
    void writeEnumUnion() throws IOException {
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
        assertThat(data.get(0).a()).isEqualTo(Template.TEMPLATE_ENUM_UNION);
        var model = (TypescriptTypeFileWriter.EnumUnionExpr) data.get(0).b();
        assertThat(model.name()).isEqualTo("EnumA");
        assertThat(model.values()).containsExactly("\"Value1\"", "123");

        when(ctxMocks.getElements().getConstantExpression(123)).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> writer.write(List.of(enumDef)))
            .hasMessageContaining("Failed to get constant expression for enum value: 123 of type int in enum");
    }

    @Test
    void writeInterface() throws IOException {
        when(ctxMocks.getContext().createSourceOutput("types.d.ts")).thenReturn(fileObject);
        var classDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.ClassA")
            .simpleName("ClassA")
            .typeVariables(List.of(
                TypeVariableInfo.builder().name("T").build(),
                TypeVariableInfo.builder().name("U").build()
            ))
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.SuperClassA")
                    .simpleName("SuperClassA")
                    .typeArgs(List.of(TypeVariableInfo.builder().name("U").build()))
                    .build()
            ))
            .components(List.of(
                FieldComponentInfo.builder().name("field1").type(INT_TYPE_INFO).optional(true).build(),
                FieldComponentInfo.builder().name("field2").type(STRING_TYPE_INFO).optional(false).build(),
                FieldComponentInfo.builder().name("field3")
                    .type(
                        new ArrayTypeInfo(
                            new ArrayTypeInfo(
                                ConcreteTypeInfo.builder()
                                    .qualifiedName("com.github.cuzfrog.Container")
                                    .simpleName("Container")
                                    .typeArgs(List.of(TypeVariableInfo.builder().name("T").build()))
                                    .build()
                            )
                        )
                    )
                    .build(),
                FieldComponentInfo.builder().name("field4")
                    .type(new ArrayTypeInfo(TypeVariableInfo.builder().name("T").build()))
                    .build()
            ))
            .build();

        writer.write(List.of(classDef));
        verify(renderer).render(any(), renderDataCaptor.capture());
        var data = renderDataCaptor.getValue();
        assertThat(data).hasSize(1);
        assertThat(data.get(0).a()).isEqualTo(Template.TEMPLATE_INTERFACE);
        var model = (TypescriptTypeFileWriter.InterfaceExpr) data.get(0).b();
        assertThat(model.name()).isEqualTo("ClassA");
        assertThat(model.typeParameters()).containsExactly("T", "U");
        assertThat(model.supertypes()).containsExactly("SuperClassA<U>");
        assertThat(model.properties()).hasSize(4);
        var prop1 = model.properties().get(0);
        assertThat(prop1.name()).isEqualTo("field1");
        assertThat(prop1.type()).isEqualTo("number");
        assertThat(prop1.optional()).isTrue();

        var prop2 = model.properties().get(1);
        assertThat(prop2.name()).isEqualTo("field2");
        assertThat(prop2.type()).isEqualTo("string");
        assertThat(prop2.optional()).isFalse();

        var prop3 = model.properties().get(2);
        assertThat(prop3.name()).isEqualTo("field3");
        assertThat(prop3.type()).isEqualTo("Container<T>[][]");
        assertThat(prop3.optional()).isFalse();

        var prop4 = model.properties().get(3);
        assertThat(prop4.name()).isEqualTo("field4");
        assertThat(prop4.type()).isEqualTo("T[]");
        assertThat(prop4.optional()).isFalse();
    }
}

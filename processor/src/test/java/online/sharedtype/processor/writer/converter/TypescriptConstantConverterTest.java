package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.writer.render.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TypescriptConstantConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptConstantConverter converter = new TypescriptConstantConverter(ctxMocks.getContext());

    private final ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder()
        .simpleName("Abc")
        .qualifiedName("com.github.cuzfrog.Abc")
        .constants(List.of(
            new ConstantField("VALUE1", Constants.BOOLEAN_TYPE_INFO, true),
            new ConstantField("VALUE2", Constants.STRING_TYPE_INFO, "value2"),
            new ConstantField("VALUE3", Constants.FLOAT_TYPE_INFO, 3.5f)
        ))
        .build();
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        ctxMocks.getTypeStore().saveConfig("com.github.cuzfrog.Abc", config);
    }

    @Test
    void convertToConstObject() {
        var tuple = converter.convert(constantNamespaceDef);
        var value = (TypescriptConstantConverter.ConstantNamespaceExpr)tuple.b();
        assertThat(value.name).isEqualTo("Abc");
        assertThat(value.constants).hasSize(3).satisfiesExactly(
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE1");
                assertThat(constantExpr.value).isEqualTo("true");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE2");
                assertThat(constantExpr.value).isEqualTo("\"value2\"");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE3");
                assertThat(constantExpr.value).isEqualTo("3.5");
            }
        );
    }

    @Test
    void useInlineTemplate() {
        when(config.isTypescriptConstantInlined()).thenReturn(true);
        var tuple = converter.convert(constantNamespaceDef);
        assertThat(tuple.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_CONSTANT_INLINE);
    }
}

package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.value.ValueHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConstantConverterTypescriptTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final ConstantConverter typescriptConverter = new ConstantConverter(ctxMocks.getContext(), null, OutputTarget.TYPESCRIPT);

    private final ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder()
        .simpleName("Abc")
        .qualifiedName("com.github.cuzfrog.Abc")
        .constants(List.of(
            new ConstantField("VALUE1", ValueHolder.of(Constants.BOOLEAN_TYPE_INFO, true)),
            new ConstantField("VALUE2", ValueHolder.of(Constants.STRING_TYPE_INFO, "value2")),
            new ConstantField("VALUE3", ValueHolder.of(Constants.FLOAT_TYPE_INFO, 3.5f))
        ))
        .build();
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        when(config.getQualifiedName()).thenReturn("com.github.cuzfrog.Abc");
        ctxMocks.getTypeStore().saveConfig(config);
        when(config.isConstantNamespaced()).thenReturn(true);
    }

    @Test
    void convertToConstObject() {
        var tuple = typescriptConverter.convert(constantNamespaceDef);
        var value = (ConstantConverter.ConstantNamespaceExpr)tuple.b();
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
        var template = tuple.a();
        assertThat(template.getOutputTarget()).isEqualTo(OutputTarget.TYPESCRIPT);
        assertThat(template.getResourcePath()).contains("constant.mustache");
    }

    @Test
    void useInlineTemplate() {
        when(config.isConstantNamespaced()).thenReturn(false);
        var tuple = typescriptConverter.convert(constantNamespaceDef);
        var template = tuple.a();
        assertThat(template.getOutputTarget()).isEqualTo(OutputTarget.TYPESCRIPT);
        assertThat(template.getResourcePath()).contains("constant-inline");
    }
}

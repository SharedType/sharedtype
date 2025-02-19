package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConstantConverterRustTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeExpressionConverter typeExpressionConverter = TypeExpressionConverter.rustLiteral();
    private final ConstantConverter typescriptConverter = new ConstantConverter(ctxMocks.getContext(), typeExpressionConverter, OutputTarget.RUST);

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
        when(config.isConstantNamespaced()).thenReturn(true);
    }

    @Test
    void interpretType() {
        var tuple = typescriptConverter.convert(constantNamespaceDef);
        var value = (ConstantConverter.ConstantNamespaceExpr)tuple.b();
        assertThat(value.constants).hasSize(3).satisfiesExactly(
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE1");
                assertThat(constantExpr.type).isEqualTo("bool");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE2");
                assertThat(constantExpr.type).isEqualTo("&str");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE3");
                assertThat(constantExpr.type).isEqualTo("f32");
            }
        );
        var template = tuple.a();
        assertThat(template.getOutputTarget()).isEqualTo(OutputTarget.RUST);
    }
}

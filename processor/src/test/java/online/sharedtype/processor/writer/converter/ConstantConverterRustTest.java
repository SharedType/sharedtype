package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.value.ValueHolder;
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
    private final ConstantConverter typescriptConverter = new ConstantConverter(ctxMocks.getContext(), typeExpressionConverter, SharedType.TargetType.RUST);

    private final ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder()
        .simpleName("Abc")
        .qualifiedName("com.github.cuzfrog.Abc")
        .constants(List.of(
            ConstantField.builder().name("VALUE1").value(ValueHolder.of(Constants.BOOLEAN_TYPE_INFO, true)).build(),
            ConstantField.builder().name("VALUE2").value(ValueHolder.of(Constants.STRING_TYPE_INFO, "value2")).build(),
            ConstantField.builder().name("VALUE3").value(ValueHolder.of(Constants.FLOAT_TYPE_INFO, 3.5f)).build(),
            ConstantField.builder().name("VALUE4").value(ValueHolder.ofEnum("ENUM_CONST", Constants.BOXED_INT_TYPE_INFO, 1)).build()
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
    void interpretType() {
        var tuple = typescriptConverter.convert(constantNamespaceDef);
        var value = (ConstantConverter.ConstantNamespaceExpr)tuple.b();
        assertThat(value.constants).hasSize(4).satisfiesExactly(
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE1");
                assertThat(constantExpr.type).isEqualTo("bool");
                assertThat(constantExpr.value).isEqualTo("true");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE2");
                assertThat(constantExpr.type).isEqualTo("&'static str");
                assertThat(constantExpr.value).isEqualTo("\"value2\"");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE3");
                assertThat(constantExpr.type).isEqualTo("f32");
                assertThat(constantExpr.value).isEqualTo("3.5");
            },
            constantExpr -> {
                assertThat(constantExpr.name).isEqualTo("VALUE4");
                assertThat(constantExpr.type).isEqualTo("i32");
                assertThat(constantExpr.value).isEqualTo("1");
            }
        );
        var template = tuple.a();
        assertThat(template.getTargetType()).isEqualTo(SharedType.TargetType.RUST);
    }
}

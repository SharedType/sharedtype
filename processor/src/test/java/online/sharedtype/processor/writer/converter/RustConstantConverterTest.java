package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static online.sharedtype.processor.context.TestUtils.typeCast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class RustConstantConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeExpressionConverter typeExpressionConverter = TypeExpressionConverter.rustLiteral(ctxMocks.getContext());
    private final RustConstantConverter constantConverter = new RustConstantConverter(ctxMocks.getContext(), typeExpressionConverter);

    private final ConcreteTypeInfo enumType = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA").build();
    private final ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder()
        .simpleName("Abc")
        .qualifiedName("com.github.cuzfrog.Abc")
        .constants(List.of(
            ConstantField.builder().name("VALUE1").value(ValueHolder.of(Constants.BOOLEAN_TYPE_INFO, true)).build(),
            ConstantField.builder().name("VALUE2").value(ValueHolder.of(Constants.STRING_TYPE_INFO, "value2")).build(),
            ConstantField.builder().name("VALUE3").value(ValueHolder.of(Constants.FLOAT_TYPE_INFO, 3.5f)).build(),
            ConstantField.builder().name("VALUE4").value(ValueHolder.ofEnum(enumType, "ENUM_CONST", Constants.BOXED_INT_TYPE_INFO, 1)).build()
        ))
        .build();
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        when(config.getQualifiedName()).thenReturn("com.github.cuzfrog.Abc");
        when(config.getRustConstKeyword()).thenReturn("const");
        ctxMocks.getTypeStore().saveConfig(config);
        when(config.isConstantNamespaced()).thenReturn(true);

        EnumDef enumDef = EnumDef.builder().simpleName("EnumA").build();
        enumType.markShallowResolved(enumDef);
    }

    @Test
    void interpretType() {
        var tuple = constantConverter.convert(constantNamespaceDef);
        RustConstantConverter.ConstantNamespaceExpr<RustConstantConverter.ConstantExpr> value = typeCast(tuple.b());
        assertThat(value.constants).hasSize(4).satisfiesExactly(
            constantExpr -> {
                assertThat(constantExpr.keyword).isEqualTo("const");
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

    @Test
    void useEnumValueTypeAlias() {
        EnumDef enumDef = EnumDef.builder().simpleName("EnumA")
            .enumValueInfos(List.of(
                EnumValueInfo.builder().value(ValueHolder.ofEnum(enumType, "Value1", Constants.INT_TYPE_INFO, 1)).build()
            ))
            .build();
        enumType.markShallowResolved(enumDef);

        var tuple = constantConverter.convert(constantNamespaceDef);
        RustConstantConverter.ConstantNamespaceExpr<RustConstantConverter.ConstantExpr> value = typeCast(tuple.b());
        var constantExpr4 = value.constants.get(3);
        assertThat(constantExpr4.name).isEqualTo("VALUE4");
        assertThat(constantExpr4.type).isEqualTo("EnumAValue");
        assertThat(constantExpr4.value).isEqualTo("1");
    }
}

package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TestUtils;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class RustEnumConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeExpressionConverter rustTypeExpressionConverter = mock(TypeExpressionConverter.class);
    private final RustMacroTraitsGenerator rustMacroTraitsGenerator = mock(RustMacroTraitsGenerator.class);
    private final RustEnumConverter converter = new RustEnumConverter(rustTypeExpressionConverter, rustMacroTraitsGenerator);

    private final Config config = mock(Config.class);
    @BeforeEach
    void setup() {
        when(config.getAnno()).thenReturn(TestUtils.defaultSharedTypeAnnotation());
    }

    @Test
    void skipEmptyEnum() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .build();
        assertThat(converter.shouldAccept(enumDef)).isFalse();
    }

    @Test
    void convertSimpleEnum() {
        ConcreteTypeInfo enumType = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA")
            .kind(ConcreteTypeInfo.Kind.ENUM).build();
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(Arrays.asList(
                EnumValueInfo.builder().name("Value1").value(ValueHolder.ofEnum("Value1", enumType, "Value1")).build(),
                EnumValueInfo.builder().name("Value2").value(ValueHolder.ofEnum("Value2", enumType, "Value2")).build()
            ))
            .build();
        enumDef.linkTypeInfo(enumType);
        when(ctxMocks.getTypeStore().getConfig(enumDef)).thenReturn(config);

        var data = converter.convert(enumDef);
        assertThat(data).isNotNull();

        var model = (RustEnumConverter.EnumExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.macroTraits).isEmpty();
        assertThat(model.hasValue).isFalse();
        assertThat(model.valueType).isNull();
        assertThat(model.enumerations).satisfiesExactly(
            v1 -> {
                assertThat(v1.name).isEqualTo("Value1");
                assertThat(v1.value).isEqualTo("EnumA::Value1");
            },
            v2 -> {
                assertThat(v2.name).isEqualTo("Value2");
                assertThat(v2.value).isEqualTo("EnumA::Value2");
            }
        );
    }

    @Test
    void convertEnumWithValues() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(Arrays.asList(
                EnumValueInfo.builder().name("Value1").value(ValueHolder.ofEnum("Value1", INT_TYPE_INFO, 11)).build(),
                EnumValueInfo.builder().name("Value2").value(ValueHolder.ofEnum("Value2", INT_TYPE_INFO, 22)).build(),
                EnumValueInfo.builder().name("Value3").value(ValueHolder.ofEnum("Value3", INT_TYPE_INFO, 33)).build()
            ))
            .build();
        enumDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").build());
        when(ctxMocks.getTypeStore().getConfig(enumDef)).thenReturn(config);
        when(rustMacroTraitsGenerator.generate(enumDef)).thenReturn(Set.of("TestMacro"));
        when(rustTypeExpressionConverter.toTypeExpr(INT_TYPE_INFO, enumDef)).thenReturn("i32");

        var data = converter.convert(enumDef);
        assertThat(data).isNotNull();

        var model = (RustEnumConverter.EnumExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.macroTraits).containsExactly("TestMacro");
        assertThat(model.hasValue).isTrue();
        assertThat(model.valueType).isEqualTo("i32");
        assertThat(model.enumerations).satisfiesExactly(
            v1 -> {
                assertThat(v1.name).isEqualTo("Value1");
                assertThat(v1.value).isEqualTo("11");
            },
            v2 -> {
                assertThat(v2.name).isEqualTo("Value2");
                assertThat(v2.value).isEqualTo("22");
            },
            v3 -> {
                assertThat(v3.name).isEqualTo("Value3");
                assertThat(v3.value).isEqualTo("33");
            }
        );
    }
}

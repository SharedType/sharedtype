package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TestUtils;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static online.sharedtype.processor.domain.Constants.BOXED_INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
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
        ConcreteTypeInfo enumType = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").build();
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(Arrays.asList(
                new EnumValueInfo("Value1", enumType, ValueHolder.ofEnum("Value1")),
                new EnumValueInfo("Value2", enumType, ValueHolder.ofEnum("Value2"))
            ))
            .build();
        enumDef.linkTypeInfo(enumType);
        when(ctxMocks.getTypeStore().getConfig(enumDef)).thenReturn(config);

        var data = converter.convert(enumDef);
        assertThat(data).isNotNull();

        var model = (RustEnumConverter.EnumExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.macroTraits).isEmpty();
        assertThat(model.hasLiteralValue).isFalse();
        assertThat(model.valueType).isNull();
        assertThat(model.enumerations).satisfiesExactly(
            v1 -> {
                assertThat(v1.name).isEqualTo("Value1");
                assertThat(v1.value).isEqualTo("\"Value1\"");
            },
            v2 -> {
                assertThat(v2.name).isEqualTo("Value2");
                assertThat(v2.value).isEqualTo("\"Value2\"");
            }
        );
    }

    @Test
    void convertEnumWithValues() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(Arrays.asList(
                new EnumValueInfo("Value1", INT_TYPE_INFO, ValueHolder.ofEnum("Value1", INT_TYPE_INFO, 11)),
                new EnumValueInfo("Value2", INT_TYPE_INFO, ValueHolder.ofEnum("Value2", INT_TYPE_INFO, 22)),
                new EnumValueInfo("Value3", INT_TYPE_INFO, ValueHolder.ofEnum("Value3", INT_TYPE_INFO, 33))
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
        assertThat(model.hasLiteralValue).isTrue();
        assertThat(model.valueType).isEqualTo("i32");
        assertThat(model.isStringType).isFalse();
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

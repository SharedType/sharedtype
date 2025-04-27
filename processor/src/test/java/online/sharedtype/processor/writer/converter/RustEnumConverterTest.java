package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TestUtils;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static online.sharedtype.processor.domain.Constants.BOXED_INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class RustEnumConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustEnumConverter converter = new RustEnumConverter(ctxMocks.getContext());

    private final Config config = mock(Config.class);

    @Test
    void skipEmptyEnum() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .build();
        assertThat(converter.shouldAccept(enumDef)).isFalse();
    }

    @Test
    void convert() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(Arrays.asList(
                new EnumValueInfo("Value1", STRING_TYPE_INFO, ValueHolder.ofEnum("Value1", STRING_TYPE_INFO, "Value1")),
                new EnumValueInfo("Value2", INT_TYPE_INFO, ValueHolder.ofEnum("Value2", INT_TYPE_INFO, 123)),
                new EnumValueInfo("Value3", BOXED_INT_TYPE_INFO, ValueHolder.ofEnum("Value3", BOXED_INT_TYPE_INFO, null))
            ))
            .build();
        when(ctxMocks.getTypeStore().getConfig(enumDef)).thenReturn(config);
        when(config.getAnno()).thenReturn(TestUtils.defaultSharedTypeAnnotation());

        var data = converter.convert(enumDef);
        assertThat(data).isNotNull();

        var model = (RustEnumConverter.EnumExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.enumerations).satisfiesExactly(
            v1 -> assertThat(v1.name).isEqualTo("Value1"),
            v2 -> assertThat(v2.name).isEqualTo("Value2"),
            v3 -> assertThat(v3.name).isEqualTo("Value3")
        );
    }

    @Test
    void macroTraits() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .build();

        Config config = new Config(
            ctxMocks.typeElement("com.github.cuzfrog.EnumA")
                .withAnnotation(SharedType.class, anno -> when(anno.rustMacroTraits()).thenReturn(new String[]{"PartialEq", "Clone"}))
                .element(),
            ctxMocks.getContext()
        );
        when(ctxMocks.getTypeStore().getConfig(enumDef)).thenReturn(config);

        var data = converter.convert(enumDef);
        var model = (RustEnumConverter.EnumExpr) data.b();
        assertThat(model.macroTraits).contains("Debug", "PartialEq", "Clone");
        assertThat(model.macroTraitsExpr()).isEqualTo("#[derive(Debug, PartialEq, Clone)]");
    }
}

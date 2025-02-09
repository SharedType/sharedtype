package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

final class RustEnumConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustEnumConverter converter = new RustEnumConverter(ctxMocks.getContext());

    @Test
    void convert() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .enumValueInfos(Arrays.asList(
                new EnumValueInfo("Value1", STRING_TYPE_INFO, "Value1"),
                new EnumValueInfo("Value2", INT_TYPE_INFO, 123),
                new EnumValueInfo("Value3", INT_TYPE_INFO, null)
            ))
            .build();
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
                .element()
        );
        when(ctxMocks.getTypeStore().getConfig(enumDef.qualifiedName())).thenReturn(config);

        var data = converter.convert(enumDef);
        var model = (RustEnumConverter.EnumExpr) data.b();
        assertThat(model.macroTraits).contains("Debug", "PartialEq", "Clone");
        assertThat(model.macroTraitsExpr()).isEqualTo("#[derive(Debug, PartialEq, Clone)]");
    }
}

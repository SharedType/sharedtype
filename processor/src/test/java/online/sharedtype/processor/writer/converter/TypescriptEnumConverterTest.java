package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.writer.render.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static online.sharedtype.processor.domain.Constants.BOXED_INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TypescriptEnumConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptEnumConverter converter = new TypescriptEnumConverter(ctxMocks.getContext());

    private final Config config = mock(Config.class);
    private final EnumDef enumDef = EnumDef.builder()
        .simpleName("EnumA")
        .qualifiedName("com.github.cuzfrog.EnumA")
        .enumValueInfos(Arrays.asList(
            EnumValueInfo.builder().name("Value1").value(ValueHolder.ofEnum(null, "Value1", STRING_TYPE_INFO, "Value1")).build(),
            EnumValueInfo.builder().name("Value2").value(ValueHolder.ofEnum(null, "Value2", INT_TYPE_INFO, 123)).build(),
            EnumValueInfo.builder().name("Value3").value(ValueHolder.ofEnum(null, "Value3", BOXED_INT_TYPE_INFO, null)).build()
        ))
        .build();

    @BeforeEach
    void setup() {
        when(ctxMocks.getTypeStore().getConfig(enumDef)).thenReturn(config);
    }

    @Test
    void writeEnumUnion() {
        when(config.getTypescriptEnumFormat()).thenReturn(Props.Typescript.EnumFormat.UNION);

        var data = converter.convert(enumDef);
        assertThat(data).isNotNull();

        assertThat(data.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_UNION_TYPE_ENUM);
        TypescriptEnumConverter.EnumUnionExpr model = (TypescriptEnumConverter.EnumUnionExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.values).containsExactly("\"Value1\"", "123", "null");
    }

    @Test
    void writeConstEnum() {
        when(config.getTypescriptEnumFormat()).thenReturn(Props.Typescript.EnumFormat.CONST_ENUM);

        var data = converter.convert(enumDef);
        assertThat(data).isNotNull();

        assertThat(data.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_ENUM);
        TypescriptEnumConverter.EnumExpr model = (TypescriptEnumConverter.EnumExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.isConst).isTrue();
        assertThat(model.values).satisfiesExactly(
            v1 -> {
                assertThat(v1.name).isEqualTo("Value1");
                assertThat(v1.value).isEqualTo("\"Value1\"");
            },
            v2 -> {
                assertThat(v2.name).isEqualTo("Value2");
                assertThat(v2.value).isEqualTo("123");
            },
            v3 -> {
                assertThat(v3.name).isEqualTo("Value3");
                assertThat(v3.value).isEqualTo("null");
            }
        );
    }

    @Test
    void skipEnumWithNoValues() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .build();
        assertThat(converter.shouldAccept(enumDef)).isFalse();
    }
}

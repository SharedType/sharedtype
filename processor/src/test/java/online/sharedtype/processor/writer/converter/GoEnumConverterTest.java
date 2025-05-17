package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GoEnumConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeExpressionConverter typeExpressionConverter = mock(TypeExpressionConverter.class);
    private final GoEnumConverter converter = new GoEnumConverter(ctxMocks.getContext(), typeExpressionConverter);

    private final Config config = mock(Config.class);
    private final ConcreteTypeInfo enumTypeInfo = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA").build();

    @Test
    void convertEnumWithIntValueConstTemplate() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .typeInfo(enumTypeInfo)
            .enumValueInfos(Arrays.asList(
                EnumValueInfo.builder().name("Value1").value(ValueHolder.ofEnum("Value1", INT_TYPE_INFO, 11)).build(),
                EnumValueInfo.builder().name("Value2").value(ValueHolder.ofEnum("Value2", INT_TYPE_INFO, 22)).build()
            ))
            .build();

        when(typeExpressionConverter.toTypeExpr(INT_TYPE_INFO, enumDef)).thenReturn("int32");
        when(ctxMocks.getContext().getTypeStore().getConfig(enumDef)).thenReturn(config);
        when(config.getGoEnumFormat()).thenReturn(Props.Go.EnumFormat.CONST);

        Tuple<Template, Object> tuple = converter.convert(enumDef);
        assertThat(tuple.a()).isEqualTo(Template.TEMPLATE_GO_CONST_ENUM);
        GoEnumConverter.EnumExpr value = (GoEnumConverter.EnumExpr) tuple.b();
        assertThat(value.name).isEqualTo("EnumA");
        assertThat(value.valueType).isEqualTo("int32");

        var enumConst1 = value.enumerations.get(0);
        assertThat(enumConst1.name).isEqualTo("Value1");
        assertThat(enumConst1.enumName).isEqualTo("EnumA");
        assertThat(enumConst1.value).isEqualTo("11");

        var enumConst2 = value.enumerations.get(1);
        assertThat(enumConst2.name).isEqualTo("Value2");
        assertThat(enumConst2.enumName).isEqualTo("EnumA");
        assertThat(enumConst2.value).isEqualTo("22");
    }

    @Test
    void convertSimpleEnumStructTemplate() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .typeInfo(enumTypeInfo)
            .enumValueInfos(Arrays.asList(
                EnumValueInfo.builder().name("Value1").value(ValueHolder.ofEnum("Value1", enumTypeInfo, "Value1")).build(),
                EnumValueInfo.builder().name("Value2").value(ValueHolder.ofEnum("Value2", enumTypeInfo, "Value2")).build()
            ))
            .build();

        when(ctxMocks.getContext().getTypeStore().getConfig(enumDef)).thenReturn(config);
        when(config.getGoEnumFormat()).thenReturn(Props.Go.EnumFormat.STRUCT);

        Tuple<Template, Object> tuple = converter.convert(enumDef);
        assertThat(tuple.a()).isEqualTo(Template.TEMPLATE_GO_STRUCT_ENUM);
        GoEnumConverter.EnumExpr value = (GoEnumConverter.EnumExpr) tuple.b();
        assertThat(value.name).isEqualTo("EnumA");
        assertThat(value.valueType).isEqualTo("string");
        assertThat(value.enumerations).satisfiesExactly(
            v1 -> {
                assertThat(v1.name).isEqualTo("Value1");
                assertThat(v1.enumName).isEqualTo("EnumA");
                assertThat(v1.value).isEqualTo("\"Value1\"");
            },
            v2 -> {
                assertThat(v2.name).isEqualTo("Value2");
                assertThat(v2.enumName).isEqualTo("EnumA");
                assertThat(v2.value).isEqualTo("\"Value2\"");
            }
        );
    }
}

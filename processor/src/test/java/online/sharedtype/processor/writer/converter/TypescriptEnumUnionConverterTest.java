package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.writer.render.Template;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;

final class TypescriptEnumUnionConverterTest {
    private final TypescriptEnumUnionConverter converter = new TypescriptEnumUnionConverter();

    @Test
    void writeEnumUnion() {
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

        assertThat(data.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_ENUM_UNION);
        EnumUnionExpr model = (EnumUnionExpr) data.b();
        assertThat(model.name).isEqualTo("EnumA");
        assertThat(model.values).containsExactly("\"Value1\"", "123", "null");
    }

    @Test
    void skipEnumWithNoValues() {
        EnumDef enumDef = EnumDef.builder()
            .simpleName("EnumA")
            .qualifiedName("com.github.cuzfrog.EnumA")
            .build();
        var data = converter.convert(enumDef);
        assertThat(data.a()).isEqualTo(Template.NULL_TEMPLATE);
    }
}

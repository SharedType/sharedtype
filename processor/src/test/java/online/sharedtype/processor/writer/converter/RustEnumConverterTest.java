package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

final class RustEnumConverterTest {
    private final RustEnumConverter converter = new RustEnumConverter();

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
}

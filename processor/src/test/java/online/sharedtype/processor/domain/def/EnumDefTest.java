package online.sharedtype.processor.domain.def;

import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class EnumDefTest {
    private final ConcreteTypeInfo enumTypeInfo = ConcreteTypeInfo.builder().simpleName("EnumA").kind(ConcreteTypeInfo.Kind.ENUM).build();

    @Test
    void componentValueTypeIsNullForEmptyEnum() {
        EnumDef enumDef = EnumDef.builder()
            .qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA")
            .enumValueInfos(Collections.emptyList())
            .typeInfo(enumTypeInfo)
            .build();
        assertThat(enumDef.getComponentValueType()).isEqualTo(enumTypeInfo);
    }

    @Test
    void componentValueType() {
        EnumDef enumDef = EnumDef.builder()
            .qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA")
            .enumValueInfos(Collections.singletonList(
                EnumValueInfo.builder().name("Value1")
                    .value(ValueHolder.ofEnum(enumTypeInfo, "Value1", Constants.BOOLEAN_TYPE_INFO, true))
                    .build()
            ))
            .typeInfo(enumTypeInfo)
            .build();
        assertThat(enumDef.getComponentValueType()).isEqualTo(Constants.BOOLEAN_TYPE_INFO);
    }
}

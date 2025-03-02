package online.sharedtype.processor.resolver;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.FieldComponentInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

final class OptionalTypeResolverTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final OptionalTypeResolver resolver = new OptionalTypeResolver(ctxMocks.getContext());

    @BeforeEach
    void setup() {
        when(ctxMocks.getContext().isOptionalType("java.util.Optional")).thenReturn(true);
    }

    @Test
    void resolveComplexNestedOptionalTypes() {
        ClassDef classDef = ClassDef.builder()
            .components(List.of(
                FieldComponentInfo.builder().name("optionalField1")
                    .type(
                        ConcreteTypeInfo.builder().qualifiedName("java.util.Optional").simpleName("Optional")
                            .typeArgs(new ArrayList<>(List.of(
                                new ArrayTypeInfo(
                                    ConcreteTypeInfo.builder()
                                        .qualifiedName("java.util.Map").simpleName("Map")
                                        .typeArgs(new ArrayList<>(List.of(
                                            Constants.BOXED_INT_TYPE_INFO,
                                            ConcreteTypeInfo.builder().qualifiedName("java.util.Optional").simpleName("Optional")
                                                .typeArgs(new ArrayList<>(List.of(Constants.STRING_TYPE_INFO)))
                                                .build()
                                        )))
                                        .build()
                                )
                            )))
                            .build()
                    )
                    .build(),
                FieldComponentInfo.builder().name("field2").type(Constants.BOOLEAN_TYPE_INFO).build()
            ))
            .build();

        var res = resolver.resolve(List.of(classDef));
        assertThat(res).hasSize(1);
        ClassDef resTypeDef = (ClassDef) res.get(0);

        assertThat(resTypeDef.components()).hasSize(2);
        var optionalField1 = resTypeDef.components().get(0);
        assertThat(optionalField1.name()).isEqualTo("optionalField1");
        assertThat(optionalField1.optional()).isTrue();
        assertThat(optionalField1.type()).isInstanceOf(ArrayTypeInfo.class);
        var field1Component = (ConcreteTypeInfo)((ArrayTypeInfo) optionalField1.type()).component();
        assertThat(field1Component.qualifiedName()).isEqualTo("java.util.Map");
        assertThat(field1Component.typeArgs()).hasSize(2);
        assertThat(field1Component.typeArgs().get(0)).isEqualTo(Constants.BOXED_INT_TYPE_INFO);
        assertThat(field1Component.typeArgs().get(1)).isEqualTo(Constants.STRING_TYPE_INFO);

        var field2 = resTypeDef.components().get(1);
        assertThat(field2.name()).isEqualTo("field2");
        assertThat(field2.optional()).isFalse();
        assertThat(field2.type()).isEqualTo(Constants.BOOLEAN_TYPE_INFO);
    }

}

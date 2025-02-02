package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TypeVariableInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class TypescriptTypeExpressionConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptTypeExpressionConverter converter = new TypescriptTypeExpressionConverter(ctxMocks.getContext());

    @Test
    void typeContract() {
        assertThat(converter.typeNameMappings.keySet()).containsAll(Constants.STRING_AND_NUMBER_TYPES);
    }

    @Test
    void enumKeyWithPartialRecordMapSpec() {
        ConcreteTypeInfo enumTypeInfo = ConcreteTypeInfo.builder().simpleName("MyEnum").enumType(true).build();
        var mapSpec = converter.mapSpec(enumTypeInfo);
        assertThat(mapSpec.prefix).isEqualTo("Partial<Record<");
        assertThat(mapSpec.delimiter).isEqualTo(", ");
        assertThat(mapSpec.suffix).isEqualTo(">>");
    }

    @Test
    void invalidKeyType() {
        ClassDef contextTypeDef = ClassDef.builder().qualifiedName("a.b.Abc").simpleName("Abc").build();
        ConcreteTypeInfo invalidKeyTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("java.util.Map").simpleName("Map")
            .mapType(true)
            .typeArgs(List.of(
                ConcreteTypeInfo.builder().qualifiedName("a.b.Foo").simpleName("Foo").build(),
                Constants.INT_TYPE_INFO
            ))
            .typeDef(
                ClassDef.builder().qualifiedName("java.util.Map").simpleName("Map")
                    .typeVariables(List.of(
                        TypeVariableInfo.builder().name("K").contextTypeQualifiedName("java.util.Map").build(),
                        TypeVariableInfo.builder().name("V").contextTypeQualifiedName("java.util.Map").build()
                    ))
                    .build()
            )
            .build();
        assertThatThrownBy(() -> converter.toTypeExpr(invalidKeyTypeInfo,contextTypeDef))
            .hasMessageContaining("Key type of java.util.Map must be string or numbers or enum")
            .hasMessageContaining("context type: a.b.Abc");
    }

    @Test
    void mapTypeHasWrongNumberOfTypeParameters() {
        ClassDef contextTypeDef = ClassDef.builder().qualifiedName("a.b.Abc").simpleName("Abc").build();
        ConcreteTypeInfo invalidMapTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("java.util.Map").simpleName("Map")
            .mapType(true)
            .typeArgs(List.of(
                Constants.INT_TYPE_INFO,
                Constants.STRING_TYPE_INFO,
                Constants.BOXED_LONG_TYPE_INFO
            ))
            .typeDef(
                ClassDef.builder().qualifiedName("java.util.Map").simpleName("Map")
                    .typeVariables(List.of(
                        TypeVariableInfo.builder().name("K").contextTypeQualifiedName("java.util.Map").build(),
                        TypeVariableInfo.builder().name("V").contextTypeQualifiedName("java.util.Map").build(),
                        TypeVariableInfo.builder().name("W").contextTypeQualifiedName("java.util.Map").build()
                    ))
                    .build()
            )
            .build();
        assertThatThrownBy(() -> converter.toTypeExpr(invalidMapTypeInfo,contextTypeDef))
            .hasMessageContaining("Base Map type must have 2 type arguments");
    }
}

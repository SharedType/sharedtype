package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class TypescriptTypeExpressionConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptTypeExpressionConverter converter = new TypescriptTypeExpressionConverter(ctxMocks.getContext());

    private final Config config = mock(Config.class);
    private final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

    @Test
    void typeContract() {
        assertThat(converter.typeNameMappings.keySet()).containsAll(Constants.LITERAL_TYPES);
    }

    @Test
    void invalidKeyType() {
        ClassDef contextTypeDef = ClassDef.builder().qualifiedName("a.b.Abc").simpleName("Abc").build();
        ConcreteTypeInfo invalidKeyTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("java.util.Map").simpleName("Map")
            .kind(ConcreteTypeInfo.Kind.MAP)
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
        converter.toTypeExpr(invalidKeyTypeInfo,contextTypeDef);
        verify(ctxMocks.getContext()).error(any(), messageCaptor.capture(), any(Object[].class));
        assertThat(messageCaptor.getValue()).contains("Key type of %s must be string or numbers or enum");
    }

    @Test
    void mapTypeHasWrongNumberOfTypeParameters() {
        ClassDef contextTypeDef = ClassDef.builder().qualifiedName("a.b.Abc").simpleName("Abc").build();
        ConcreteTypeInfo invalidMapTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("java.util.Map").simpleName("Map")
            .kind(ConcreteTypeInfo.Kind.MAP)
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
        converter.toTypeExpr(invalidMapTypeInfo,contextTypeDef);
        verify(ctxMocks.getContext()).error(any(), messageCaptor.capture(), any(Object[].class));
        assertThat(messageCaptor.getValue()).contains("Base Map type must have 2 type arguments");
    }

    @Test
    void typeMapping() {
        ConcreteTypeInfo typeInfo = ConcreteTypeInfo.builder().qualifiedName("a.b.A1").build();
        assertThat(converter.toTypeExpression(typeInfo, "DefaultName")).isEqualTo("DefaultName");
        typeInfo.addMappedName(SharedType.TargetType.TYPESCRIPT, "AAA");
        assertThat(converter.toTypeExpression(typeInfo, "DefaultName")).isEqualTo("AAA");

        when(config.getTypescriptTargetDatetimeTypeLiteral()).thenReturn("DefaultDateLiteral");
        DateTimeInfo dateTimeInfo = new DateTimeInfo("a.b.A2");
        assertThat(converter.dateTimeTypeExpr(dateTimeInfo, config)).isEqualTo("DefaultDateLiteral");
        dateTimeInfo.addMappedName(SharedType.TargetType.TYPESCRIPT, "BBB");
        assertThat(converter.dateTimeTypeExpr(dateTimeInfo, config)).isEqualTo("BBB");
    }
}

package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.type.MapTypeInfo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
        MapTypeInfo mapTypeInfo = MapTypeInfo.builder()
            .qualifiedName("java.util.Map")
            .keyType(ConcreteTypeInfo.builder().qualifiedName("a.b.Foo").simpleName("Foo").build())
            .valueType(Constants.INT_TYPE_INFO)
            .build();
        converter.toTypeExpr(mapTypeInfo,contextTypeDef);
        verify(ctxMocks.getContext()).error(any(), messageCaptor.capture(), any(Object[].class));
        assertThat(messageCaptor.getValue()).contains("Key type of %s must be string or numbers or enum");
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

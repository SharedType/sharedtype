package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TargetCodeType;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GoTypeExpressionConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final GoTypeExpressionConverter converter = new GoTypeExpressionConverter(ctxMocks.getContext());

    private final Config config = mock(Config.class);
    private final ClassDef contextTypeDef = ClassDef.builder().simpleName("Abc").build();

    @Test
    void typeContract() {
        assertThat(converter.typeNameMappings.keySet()).containsAll(Constants.LITERAL_TYPES);
    }

    @Test
    void convertArrayType() {
        String expr = converter.toTypeExpr(new ArrayTypeInfo(Constants.INT_TYPE_INFO), contextTypeDef);
        assertThat(expr).isEqualTo("[]int32");
    }

    @Test
    void convertObjectType() {
        assertThat(converter.toTypeExpr(Constants.OBJECT_TYPE_INFO, contextTypeDef)).isEqualTo("any");
    }

    @Test
    void convertDateTimeAndTypeMappings() {
        when(config.getGoTargetDatetimeTypeLiteral()).thenReturn("DefaultDateLiteral");
        DateTimeInfo dateTimeInfo = new DateTimeInfo("a.b.A2");
        assertThat(converter.dateTimeTypeExpr(dateTimeInfo, config)).isEqualTo("DefaultDateLiteral");
        dateTimeInfo.addMappedName(TargetCodeType.GO, "BBB");
        assertThat(converter.dateTimeTypeExpr(dateTimeInfo, config)).isEqualTo("BBB");
    }

    @Test
    void convertPrimitiveType() {
        assertThat(converter.toTypeExpr(Constants.INT_TYPE_INFO, contextTypeDef)).isEqualTo("int32");
    }
}

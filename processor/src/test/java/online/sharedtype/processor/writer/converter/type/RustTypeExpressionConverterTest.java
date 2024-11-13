package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.Constants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class RustTypeExpressionConverterTest {
    private final RustTypeExpressionConverter converter = new RustTypeExpressionConverter();

    @Test
    void convertArrayType() {
        String expr = converter.toTypeExpr(new ArrayTypeInfo(Constants.INT_TYPE_INFO));
        assertThat(expr).isEqualTo("Vec<i32>");
    }
}

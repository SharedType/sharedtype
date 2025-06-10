package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.component.TagLiteralContainer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

final class AbstractFieldExprTest {

    @Test
    void splitTagLiterals() {
        var componentInfo = ConstantField.builder().tagLiterals(
            Map.of(SharedType.TargetType.GO, List.of(
                new TagLiteralContainer(List.of("a", "b"), SharedType.TagPosition.NEWLINE_ABOVE),
                new TagLiteralContainer(List.of("c", "d"), SharedType.TagPosition.INLINE_AFTER)
            ))
        ).build();
        var expr = new AbstractFieldExpr(componentInfo, SharedType.TargetType.GO){};

        assertThat(expr.tagLiteralsAbove).containsExactly("a", "b");
        assertThat(expr.tagLiteralsInlineAfter).isEqualTo("c d");
    }
}

package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
final class EnumUnionExpr {
    final String name;
    final List<String> values;

    @SuppressWarnings("unused")
    String valuesExpr() {
        return String.join(" | ", values);
    }
}

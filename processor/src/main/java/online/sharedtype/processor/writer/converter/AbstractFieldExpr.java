package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.component.ComponentInfo;
import online.sharedtype.processor.domain.component.TagLiteralContainer;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractFieldExpr {
    final String name;
    final List<String> tagLiteralsAbove;
    final String tagLiteralsInlineAfter;

    AbstractFieldExpr(ComponentInfo componentInfo, SharedType.TargetType targetType) {
        name = componentInfo.name();
        List<TagLiteralContainer> tagLiterals = componentInfo.getTagLiterals(targetType);
        this.tagLiteralsAbove = new ArrayList<>(tagLiterals.size());
        StringBuilder inlineTagsBuilder = new StringBuilder();
        for (TagLiteralContainer tagLiteral : tagLiterals) {
            if (tagLiteral.getPosition() == SharedType.TagPosition.NEWLINE_ABOVE) {
                tagLiteralsAbove.addAll(tagLiteral.getContents());
            } else if (tagLiteral.getPosition() == SharedType.TagPosition.INLINE_AFTER) {
                inlineTagsBuilder.append(String.join(" ", tagLiteral.getContents()));
            } else {
                throw new IllegalArgumentException("Unknown tag literal position: " + tagLiteral.getPosition());
            }
        }
        this.tagLiteralsInlineAfter = inlineTagsBuilder.toString();
    }
}

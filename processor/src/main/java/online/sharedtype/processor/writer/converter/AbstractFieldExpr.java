package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.component.ComponentInfo;

import java.util.List;

abstract class AbstractFieldExpr {
    final String name;
    final List<String> tagLiterals;

    AbstractFieldExpr(ComponentInfo componentInfo, SharedType.TargetType targetType) {
        name = componentInfo.name();
        tagLiterals = componentInfo.getTagLiterals(targetType);
    }
}

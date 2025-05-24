package online.sharedtype.processor.domain;

import online.sharedtype.SharedType;

import online.sharedtype.processor.support.annotation.Nullable;

public interface MappableType {
    String qualifiedName();

    @Nullable
    String mappedName(@Nullable SharedType.TargetType targetType);

    default String mappedNameOrDefault(SharedType.TargetType targetType, String defaultExpr) {
        String name = mappedName(targetType);
        return name == null ? defaultExpr : name;
    }

    void addMappedName(SharedType.TargetType targetType, String mappedName);
}

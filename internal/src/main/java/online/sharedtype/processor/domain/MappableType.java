package online.sharedtype.processor.domain;

import javax.annotation.Nullable;

public interface MappableType {
    String qualifiedName();

    @Nullable
    String mappedName(@Nullable TargetCodeType targetCodeType);

    default String mappedNameOrDefault(TargetCodeType targetCodeType, String defaultExpr) {
        String name = mappedName(targetCodeType);
        return name == null ? defaultExpr : name;
    }

    void addMappedName(TargetCodeType targetCodeType, String mappedName);
}

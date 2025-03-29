package online.sharedtype.processor.domain;

import javax.annotation.Nullable;

public interface MappableType {
    String qualifiedName();

    @Nullable
    String mappedName(@Nullable TargetCodeType targetCodeType);

    void addMappedName(TargetCodeType targetCodeType, String mappedName);
}

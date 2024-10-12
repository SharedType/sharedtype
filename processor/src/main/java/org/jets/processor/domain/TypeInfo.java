package org.jets.processor.domain;

import lombok.Builder;

import javax.annotation.Nullable;
import javax.lang.model.type.TypeMirror;
import java.util.List;

@Builder
public record TypeInfo(
        String qualifiedName,
        @Nullable String simpleName,
        List<? extends TypeMirror> typeArgs,
        boolean resolved
) {
    public static TypeInfo ofPredefined(String qualifiedName, String simpleName) {
        return TypeInfo.builder().qualifiedName(qualifiedName).simpleName(simpleName).build();
    }

    public static final class TypeInfoBuilder {
        private List<? extends TypeMirror> typeArgs = List.of();
        private boolean resolved = true;
    }
}

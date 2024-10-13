package org.jets.processor.domain;

import java.util.Set;

import javax.lang.model.element.Modifier;

import lombok.Builder;

@Builder
public record FieldInfo(
        String name,
        Set<Modifier> modifiers,
        boolean optional,
        TypeInfo typeInfo
) implements ComponentInfo {

    public boolean resolved() {
        return typeInfo.resolved();
    }
}

package org.sharedtype.processor.domain;

import lombok.Builder;

import javax.lang.model.element.Modifier;
import java.util.Set;

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

    @Override
    public String toString() {
        return String.format("%s %s%s", typeInfo, name, optional ? "?" : "");
    }
}

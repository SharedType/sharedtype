package org.sharedtype.domain;

import lombok.Builder;

import javax.lang.model.element.Modifier;
import java.util.Set;

@Builder
public record FieldComponentInfo(
        String name,
        Set<Modifier> modifiers,
        boolean optional,
        TypeInfo type
) implements ComponentInfo {

    @Override
    public boolean resolved() {
        return type.resolved();
    }

    @Override
    public String toString() {
        return String.format("%s %s%s", type, name, optional ? "?" : "");
    }
}

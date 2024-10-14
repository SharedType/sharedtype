package org.jets.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

/**
 * Represents info captured from a POJO or record.
 */
@Builder
@ToString
@EqualsAndHashCode(of = "name")
public final class ClassDef implements TypeDef {
    private final String name;
    @Builder.Default
    private final List<FieldInfo> fields = Collections.emptyList();
    @Builder.Default
    private final List<TypeVariableInfo> typeVariables = Collections.emptyList();

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<FieldInfo> components() {
        return fields;
    }

    public List<TypeVariableInfo> typeVariables() {
        return typeVariables;
    }

    // TODO: optimize
    @Override
    public boolean resolved() {
        for (FieldInfo fieldInfo : fields) {
            if (!fieldInfo.resolved()) {
                return false;
            }
        }
        return true;
    }
}

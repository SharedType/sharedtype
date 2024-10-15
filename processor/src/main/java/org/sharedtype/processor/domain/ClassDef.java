package org.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents info captured from a POJO or record.
 */
@Builder
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

    @Override
    public String toString() {
        var rows = new ArrayList<String>(fields.size()+2);
        rows.add(String.format("%s%s {", name, typeVariables.isEmpty() ? "" : "<" + String.join(",", typeVariables.stream().map(TypeVariableInfo::toString).toList()) + ">"));
        rows.addAll(fields.stream().map(f -> String.format("  %s", f)).toList());
        rows.add("}");
        return String.join(System.lineSeparator(), rows);
    }
}

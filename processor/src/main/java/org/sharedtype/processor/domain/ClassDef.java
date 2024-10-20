package org.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents info captured from an interface, class, or record.
 */
@Builder
@EqualsAndHashCode(of = "name")
public final class ClassDef implements TypeDef {
    private final String qualifiedName;
    private final String name;
    @Builder.Default
    private final List<FieldComponentInfo> components = Collections.emptyList();
    @Builder.Default
    private final List<TypeVariableInfo> typeVariables = Collections.emptyList();
    @Builder.Default
    private final List<TypeDef> supertypes = Collections.emptyList(); // direct supertypes

    @Override
    public String qualifiedName() {
        return qualifiedName;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<FieldComponentInfo> components() {
        return components;
    }

    public List<TypeVariableInfo> typeVariables() {
        return typeVariables;
    }

    public List<TypeDef> supertypes() {
        return supertypes;
    }

    // TODO: optimize
    @Override
    public boolean resolved() {
        for (FieldComponentInfo fieldComponentInfo : components) {
            if (!fieldComponentInfo.resolved()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var rows = new ArrayList<String>(components.size()+2);
        rows.add(String.format("%s%s%s {", name, typeVariablesToString(), supertypesToString()));
        rows.addAll(components.stream().map(f -> String.format("  %s", f)).toList());
        rows.add("}");
        return String.join(System.lineSeparator(), rows);
    }

    private String typeVariablesToString() {
        return typeVariables.isEmpty() ? "" : "<" + String.join(",", typeVariables.stream().map(TypeVariableInfo::toString).toList()) + ">";
    }

    private String supertypesToString() {
        return supertypes.isEmpty() ? "" : " extends " + String.join(" & ", supertypes.stream().map(TypeDef::qualifiedName).toList());
    }
}

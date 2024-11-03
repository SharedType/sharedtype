package org.sharedtype.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents structural info captured from an interface, class, or record.
 *
 * @author Cause Chung
 */
@Builder
@EqualsAndHashCode(of = "qualifiedName")
public final class ClassDef implements TypeDef {
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<FieldComponentInfo> components = Collections.emptyList();
    @Builder.Default
    private final List<TypeVariableInfo> typeVariables = Collections.emptyList();
    @Builder.Default
    private final List<TypeInfo> supertypes = Collections.emptyList(); // direct supertypes

    @Override
    public String qualifiedName() {
        return qualifiedName;
    }

    @Override
    public String simpleName() {
        return simpleName;
    }

    @Override
    public List<FieldComponentInfo> components() {
        return components;
    }

    public List<TypeVariableInfo> typeVariables() {
        return typeVariables;
    }

    public List<TypeInfo> supertypes() {
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
        for (TypeVariableInfo typeVariableInfo : typeVariables) {
            if (!typeVariableInfo.resolved()) {
                return false;
            }
        }
        for (TypeInfo supertype : supertypes) {
            if (!supertype.resolved()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        List<String> rows = new ArrayList<>(components.size()+2);
        rows.add(String.format("%s%s%s {", simpleName, typeVariablesToString(), supertypesToString()));
        rows.addAll(components.stream().map(f -> String.format("  %s", f)).collect(Collectors.toList()));
        rows.add("}");
        return String.join(System.lineSeparator(), rows);
    }

    private String typeVariablesToString() {
        return typeVariables.isEmpty() ? "" : "<" + typeVariables.stream().map(TypeVariableInfo::toString).collect(Collectors.joining(",")) + ">";
    }

    private String supertypesToString() {
        if (supertypes.isEmpty()) {
            return "";
        }
        return " extends " + supertypes.stream().map(t -> t + (t.resolved() ? "" : "?")).collect(Collectors.joining(" & "));
    }
}

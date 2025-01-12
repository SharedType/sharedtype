package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents structural info captured from an interface, class, or record. Similar concept with {@link javax.lang.model.element.TypeElement}.
 *
 * @see TypeInfo
 * @author Cause Chung
 */
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "qualifiedName")
public final class ClassDef implements TypeDef {
    private static final long serialVersionUID = 9052013791381913516L;
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

    public List<TypeInfo> directSupertypes() {
        return supertypes;
    }

    public ClassDef reify(List<? extends TypeInfo> typeArgs) {
        int l;
        if ((l = typeArgs.size()) != typeVariables.size()) {
            throw new IllegalArgumentException(String.format("Cannot reify %s against typeArgs: %s", this, typeArgs));
        }
        if (l == 0) {
            return this;
        }
        Map<TypeVariableInfo, TypeInfo> mappings = new HashMap<>(l);
        for (int i = 0; i < l; i++) {
            mappings.put(typeVariables.get(i), typeArgs.get(i));
        }
        List<FieldComponentInfo> reifiedComponents = components.stream()
            .map(comp -> comp.toBuilder().type(comp.type().reify(mappings)).build())
            .collect(Collectors.toList());
        List<TypeInfo> reifiedSupertypes = supertypes.stream()
            .map(supertype -> supertype.reify(mappings)).collect(Collectors.toList());
        return this.toBuilder()
            .components(reifiedComponents)
            .supertypes(reifiedSupertypes)
            .build();
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

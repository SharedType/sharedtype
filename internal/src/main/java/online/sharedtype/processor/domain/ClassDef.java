package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents structural info captured from an interface, class, or record. Similar concept with {@link javax.lang.model.element.TypeElement}.
 *
 * @see TypeInfo
 * @author Cause Chung
 */
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(of = "qualifiedName", callSuper = false)
public final class ClassDef extends AbstractTypeDef {
    private static final long serialVersionUID = 9052013791381913516L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<FieldComponentInfo> components = new ArrayList<>();
    @Builder.Default
    private final List<TypeVariableInfo> typeVariables = new ArrayList<>();
    @Builder.Default
    private final List<TypeInfo> supertypes = new ArrayList<>(); // direct supertypes

    /** Counterpart typeInfos, there can be multiple typeInfo instances with different reified typeArgs relating to the same typeDef. */
    private final Set<ConcreteTypeInfo> typeInfoSet = new HashSet<>();

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

    @Override
    public List<TypeInfo> directSupertypes() {
        return supertypes;
    }

    public Set<ConcreteTypeInfo> typeInfoSet() {
        return typeInfoSet;
    }

    public boolean isMapType() {
        return typeInfoSet.stream().anyMatch(ConcreteTypeInfo::isMapType);
    }

    /**
     * Register a counterpart typeInfo.
     * @see #typeInfoSet
     */
    public void linkTypeInfo(ConcreteTypeInfo typeInfo) {
        typeInfoSet.add(typeInfo);
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

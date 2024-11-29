package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents structural info captured from an interface, class, or record. Similar concept with {@link javax.lang.model.element.TypeElement}.
 *
 * @see TypeInfo
 * @author Cause Chung
 */
@Builder
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

    /**
     * Recursively gather all supertypes.
     * <br>
     * Due to a generic type can have different reified type arguments,
     * there can be multiple {@link TypeInfo} pointing to the same resolved {@link TypeDef}.
     * So when called from this TypeInfo, the actual supertype type arguments must be passed to and reified at its supertypes.
     *
     * @return all supertypes in the hierarchy with type parameters reified from this callee.
     * @throws IllegalStateException if a supertype is not resolved.
     */
    public List<TypeInfo> allSupertypes() {
        return allSupertypes(null);
    }

    private List<TypeInfo> allSupertypes(Object typeArgs) {
        List<TypeInfo> res = new ArrayList<>(supertypes.size() * 3); // TODO: cap estimate
        res.addAll(supertypes);
        for (TypeInfo supertype : supertypes) {
            if (!supertype.resolved()) {
                throw new IllegalStateException("An unresolved supertype does not have type hierarchy information." +
                    String.format(" Failed to gather deep type info from type: %s, direct unresolved super type: %s", qualifiedName, supertype));
            }
            if (supertype instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo superConcreteTypeInfo = (ConcreteTypeInfo) supertype;
                TypeDef superTypeDef = superConcreteTypeInfo.typeDef();
                if (superTypeDef instanceof ClassDef) {
                    ClassDef superClassDef = (ClassDef) superTypeDef;
                    superConcreteTypeInfo.typeArgs();
                    superClassDef.typeVariables();
                    res.addAll(superClassDef.allSupertypes(typeArgs));
                }
            }
        }
        return res;
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

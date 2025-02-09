package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a constant namespace, i.e. a java class that contains static fields.
 */
@Builder
public final class ConstantNamespaceDef implements TypeDef {
    private static final long serialVersionUID = 4249235760298548628L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<ConstantField> constants = new ArrayList<>();
    @Getter @Setter
    private boolean annotated;

    @Override
    public String qualifiedName() {
        return qualifiedName;
    }

    @Override
    public String simpleName() {
        return simpleName;
    }

    @Override
    public List<ConstantField> components() {
        return constants;
    }

    @Override
    public boolean resolved() {
        for (ConstantField constant : constants) {
            if (!constant.resolved()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCyclicReferenced() {
        return false;
    }

    @Override
    public void setCyclicReferenced(boolean cyclicReferenced) {
    }

    @Override
    public boolean isReferencedByAnnotated() {
        return false;
    }

    @Override
    public void setReferencedByAnnotated(boolean referencedByAnnotated) {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("const ").append(qualifiedName).append(" {").append(System.lineSeparator());
        for (ConstantField constant : constants) {
            sb.append("  ").append(constant).append(";").append(System.lineSeparator());
        }
        sb.append("}");
        return sb.toString();
    }
}

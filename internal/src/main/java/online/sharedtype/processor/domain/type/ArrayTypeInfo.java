package online.sharedtype.processor.domain.type;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Represents an array-like type.
 * During parsing, a predefined array-like type and its subtypes is captured as this class.
 * A type will be recognized as this type with higher priority than {@link ConcreteTypeInfo}.
 * It has no counterpart typeDef, explicitly annotating {@code @SharedType} on an array type is ignored with a warning.
 * <br>
 * Predefined array-like types can be configured in global properties. Default is {@link java.lang.Iterable}.
 *
 * @see ConcreteTypeInfo
 * @author Cause Chung
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class ArrayTypeInfo implements TypeInfo {
    private static final long serialVersionUID = -6969192495547169811L;
    private final TypeInfo component;

    public TypeInfo component() {
        return component;
    }

    @Override
    public boolean resolved() {
        return component.resolved();
    }

    @Override
    public TypeInfo reify(Map<TypeVariableInfo, TypeInfo> mappings) {
        TypeInfo reifiedComponent = component.reify(mappings);
        return reifiedComponent == component ? this : new ArrayTypeInfo(reifiedComponent);
    }

    @Override
    public String toString() {
        return component + "[]";
    }
}

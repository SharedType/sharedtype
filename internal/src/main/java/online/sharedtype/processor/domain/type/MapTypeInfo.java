package online.sharedtype.processor.domain.type;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(fluent = true, chain = false)
@Getter
@EqualsAndHashCode(of = {"keyType", "valueType"}, callSuper = false)
@Builder(toBuilder = true)
public final class MapTypeInfo extends ReferableTypeInfo {
    private static final long serialVersionUID = -488629168782059977L;
    private final String baseMapTypeQualifiedName;
    private final String qualifiedName;
    private final TypeInfo keyType;
    private final TypeInfo valueType;

    @Override
    public boolean resolved() {
        return keyType.resolved() && valueType.resolved();
    }
    @Override
    public TypeInfo reify(Map<TypeVariableInfo, TypeInfo> mappings) {
        TypeInfo reifiedKeyType = keyType.reify(mappings);
        TypeInfo reifiedValueType = valueType.reify(mappings);
        return reifiedKeyType == keyType && reifiedValueType == valueType ? this : this.toBuilder().keyType(reifiedKeyType).valueType(reifiedValueType).build();
    }

    @Override
    public String toString() {
        return String.format("%s[%s->%s]", qualifiedName, keyType, valueType);
    }
}

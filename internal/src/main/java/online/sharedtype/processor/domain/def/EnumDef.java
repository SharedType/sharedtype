package online.sharedtype.processor.domain.def;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an enum.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode(of = "qualifiedName", callSuper = false)
@SuperBuilder
public final class EnumDef extends ConcreteTypeDef {
    private static final long serialVersionUID = 9158463705652816935L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<EnumValueInfo> enumValueInfos = new ArrayList<>();

    /**
     * Corresponding typeInfo, since Enum cannot have type parameters, there can only be 1 typeInfo.
     */
    private ConcreteTypeInfo typeInfo;

    @Override
    public String qualifiedName() {
        return qualifiedName;
    }

    @Override
    public String simpleName() {
        return simpleName;
    }

    @Override
    public List<EnumValueInfo> components() {
        return enumValueInfos;
    }

    /** If the enum is empty, return this enum type. */
    public TypeInfo getComponentValueType() {
        if (enumValueInfos.isEmpty()) {
            return typeInfo;
        }
        return enumValueInfos.get(0).value().getValueType();
    }
    /** @return true if component value has a different type other than this enumType, i.e. when enumValue is defined */
    public boolean hasComponentValueType() {
        return !getComponentValueType().equals(typeInfo);
    }
    public String valueTypeAlias() {
        return String.format("%sValue", simpleName);
    }

    @Override
    public Set<ConcreteTypeInfo> typeInfoSet() {
        Set<ConcreteTypeInfo> typeInfoSet = new HashSet<>(1);
        typeInfoSet.add(typeInfo);
        return typeInfoSet;
    }

    @Override
    public void linkTypeInfo(ConcreteTypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    @Override
    public boolean resolved() {
        return enumValueInfos.stream().allMatch(EnumValueInfo::resolved);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]",
            qualifiedName,
            enumValueInfos.isEmpty() ? "" : enumValueInfos.stream().map(EnumValueInfo::toString).collect(Collectors.joining(","))
        );
    }
}

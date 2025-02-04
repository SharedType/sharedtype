package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an enum.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode(of = "qualifiedName", callSuper = false)
@SuperBuilder
public final class EnumDef extends AbstractTypeDef {
    private static final long serialVersionUID = 9158463705652816935L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<EnumValueInfo> enumValueInfos = new ArrayList<>();

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

    @Override
    public List<TypeInfo> directSupertypes() {
        return Collections.emptyList();
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

package org.jets.processor.domain;

import java.util.List;

import lombok.Builder;

/**
 * Represents info captured from a POJO or record.
 */
@Builder
public record ClassInfo(
    String name,
    List<FieldInfo> fields
) implements DefInfo {

    @Override
    public List<FieldInfo> components() {
        return fields;
    }

    // TODO: optimize
    @Override
    public boolean resolved() {
        for (FieldInfo fieldInfo : fields) {
            if (!fieldInfo.typeResolved()) {
                return true;
            }
        }
        return false;
    }
}

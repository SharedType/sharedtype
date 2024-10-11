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
) implements TypeInfo {
}

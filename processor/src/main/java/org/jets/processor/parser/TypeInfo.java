package org.jets.processor.parser;

import lombok.Builder;
import org.jets.annotation.EmitTypescript;

import javax.lang.model.element.ElementKind;
import java.util.List;

@Builder
public record TypeInfo(
    String name,
    ElementKind elementKind,
    EmitTypescript anno,
    List<ComponentInfo> components
) {
}

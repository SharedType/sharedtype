package com.github.jets.types;

import org.jets.annotation.EmitType;

@EmitType
public record JavaRecord(
    String name,
    int age,
    boolean isMan
) {
}

package com.github.jets.types;

import org.jets.annotation.EmitTypescript;

@EmitTypescript
public record JavaRecord(
    String name,
    int age,
    boolean isMan
) {
}

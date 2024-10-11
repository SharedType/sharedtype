package com.github.jets.types;

import org.jets.annotation.EmitType;

@EmitType
public record JavaRecord(
    String string,
    byte primitiveByte,
    Byte boxedByte,
    short primitiveShort,
    Short boxedShort,
    int primitiveInt,
    Integer boxedInt,
    long primitiveLong,
    Long boxedLong,
    float primitiveFloat,
    Float boxedFloat,
    double primitiveDouble,
    Double boxedDouble,
    boolean primitiveBoolean,
    Boolean boxedBoolean,
    String primitiveString,
    String boxedString,
    Object object,
    Void aVoid
) {
}

package com.github.jets.types;

import org.jets.annotation.EmitType;

import java.util.List;
import java.util.Map;

@EmitType
public record JavaRecord<T, K>(
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
    Void aVoid,

    DependencyClassA a,

    List<String> stringList,
    List<T> genericList,
    Map<K, T> genericMap
) {
}

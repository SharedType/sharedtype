package com.github.sharedtype.types;

import org.sharedtype.annotation.EmitType;

import java.util.List;
import java.util.Set;

@EmitType
public record JavaRecord<T, K>(
//    String string,
//    byte primitiveByte,
//    Byte boxedByte,
//    short primitiveShort,
//    Short boxedShort,
//    int primitiveInt,
//    Integer boxedInt,
//    long primitiveLong,
//    Long boxedLong,
//    float primitiveFloat,
//    Float boxedFloat,
//    double primitiveDouble,
//    Double boxedDouble,
//    boolean primitiveBoolean,
//    Boolean boxedBoolean,
//    String primitiveString,
//    String boxedString,
//    Object object,
//    Void aVoid,

//    DependencyClassA a, // cyclic a -> b -> c -> a

//    List<Container<String>> containerStringList,
    List<T> genericList,
    Set<T> genericSet
//    List<Set<T>> genericListSet
//    Map<K, T> genericMap
) {
}

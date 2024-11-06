package online.sharedtype.it.java17;

import online.sharedtype.SharedType;
import online.sharedtype.it.java8.Container;
import online.sharedtype.it.java8.DependencyClassA;
import online.sharedtype.it.java8.EnumGalaxy;
import online.sharedtype.it.java8.EnumSize;
import online.sharedtype.it.java8.InterfaceA;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SharedType
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
    char primitiveChar,
    Character boxedChar,

    Object object,
    Void aVoid,

    DependencyClassA cyclicDependency,// cyclic a ->b ->c ->a

    List<Container<String>> containerStringList,
    List<Collection<Container<String>>> containerStringListCollection,

    List<T> genericList,
    Set<T> genericSet,
    List<Set<T>> genericListSet,
    Map<K, T> genericMap,
    int[] intArray,
    Integer[] boxedIntArray,

    EnumGalaxy enumGalaxy,
    EnumSize enumSize,

    String duplicateAccessor,
    @SharedType.Ignore String explicitlyIgnored
) implements InterfaceA<T> {
    static final int STATIC_FIELD = 888;

    @SharedType.Accessor
    String getDuplicateAccessor() {
        return duplicateAccessor;
    }

    String shouldNotBeIncluded() {
        return null;
    }

    @Override
    public T getValue() {
        return null;
    }
}

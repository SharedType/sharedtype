package online.sharedtype.it.java17;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import online.sharedtype.SharedType;
import online.sharedtype.it.java8.Container;
import online.sharedtype.it.java8.DependencyClassA;
import online.sharedtype.it.java8.EnumGalaxy;
import online.sharedtype.it.java8.EnumSize;
import online.sharedtype.it.java8.InterfaceA;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@SharedType(
    constantNamespaced = SharedType.OptionalBool.FALSE,
    includes = {
        SharedType.ComponentType.CONSTANTS,
        SharedType.ComponentType.FIELDS,
        SharedType.ComponentType.ACCESSORS,
    },
    rustMacroTraits = {"serde::Serialize", "serde::Deserialize"}
)
@Builder
public record JavaRecord<T>(
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
//    Void aVoid,

    DependencyClassA cyclicDependency,// cyclic a ->b ->c ->a

    List<Container<String>> containerStringList,
    List<Collection<Container<String>>> containerStringListCollection,

    List<T> genericList,
    Set<T> genericSet,
    List<Set<T>> genericListSet,
//    Map<K, T> genericMap,
    int[] intArray,
    Integer[] boxedIntArray,

    EnumGalaxy enumGalaxy,
    EnumSize enumSize,

    String duplicateAccessor,
    @SharedType.Ignore @JsonIgnore String explicitlyIgnored
) implements InterfaceA<T> {
    static final int STATIC_FIELD_FROM_JAVA_RECORD = 888;

    @SharedType.Accessor
    String getDuplicateAccessor() {
        return duplicateAccessor;
    }

    String shouldNotBeIncluded() {
        return null;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Override
    public T getValue() {
        return null;
    }
}

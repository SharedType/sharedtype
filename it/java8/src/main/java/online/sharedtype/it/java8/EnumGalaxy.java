package online.sharedtype.it.java8;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

@SharedType(
    rustMacroTraits = {"PartialEq", "Eq", "Hash", "serde::Serialize", "serde::Deserialize"}
)
public enum EnumGalaxy {
    MilkyWay, Andromeda, Triangulum;
}

@RequiredArgsConstructor
@SharedType
enum EnumConstReference {
    ReferenceConstantInOther(MyConstants.LONG_VALUE),
    ReferenceConstantLocally(EnumConstReference.LONG_VALUE),
    ;
    private static final long LONG_VALUE = 156L;
    @SharedType.EnumValue
    private final long longValue;
}

@RequiredArgsConstructor
@SharedType
enum EnumEnumReference {
    ReferenceAnother(EnumSize.LARGE),
    ;
    @SharedType.EnumValue
    private final EnumSize enumValue;
}

@RequiredArgsConstructor
@SharedType
enum EnumSimpleEnumReference {
    ReferenceAnother1(EnumGalaxy.Andromeda),
    ;
    @SharedType.EnumValue
    private final EnumGalaxy enumValue;
}

@RequiredArgsConstructor
@SharedType
enum EnumEnumEnumReference {
    ReferenceAnother2(EnumSimpleEnumReference.ReferenceAnother1),
    ;
    @SharedType.EnumValue
    private final EnumSimpleEnumReference enumValue;
}

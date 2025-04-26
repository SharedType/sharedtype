package online.sharedtype.it.java8;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

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

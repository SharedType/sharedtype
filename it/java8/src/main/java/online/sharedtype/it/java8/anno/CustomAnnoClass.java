package online.sharedtype.it.java8.anno;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

@SharedType
final class CustomAnnoClass {
    @ToIgnore
    int ignoredField;

    @AsAccessor
    int someValue() {
        return 1;
    }
}

@RequiredArgsConstructor
@SharedType
enum CustomAnnoEnum {
    A(1), B(2)
    ;
    @AsEnumValue
    private final int value;
}

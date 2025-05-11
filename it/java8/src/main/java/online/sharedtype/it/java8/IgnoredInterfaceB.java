package online.sharedtype.it.java8;

import com.fasterxml.jackson.annotation.JsonIgnore;
import online.sharedtype.SharedType;

@SharedType.Ignore
interface IgnoredInterfaceB {
    @JsonIgnore
    default boolean getBooleanValue() {
        return false;
    }

    int getNotIgnoredImplementedMethod();
}

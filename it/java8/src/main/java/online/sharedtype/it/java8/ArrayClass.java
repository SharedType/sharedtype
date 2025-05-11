package online.sharedtype.it.java8;

import lombok.Data;
import online.sharedtype.SharedType;

@SharedType
@Data
public final class ArrayClass {
    private CustomList arr;
}

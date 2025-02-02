package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@SharedType
public class TempClass {
    private ConcurrentMap<EnumSize, String> mapField;
}

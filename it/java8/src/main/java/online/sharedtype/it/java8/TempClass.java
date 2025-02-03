package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import java.util.concurrent.ConcurrentMap;

@SharedType
public class TempClass {
    private ConcurrentMap<Integer, String> mapField;
    private CustomMap customMapField;
}

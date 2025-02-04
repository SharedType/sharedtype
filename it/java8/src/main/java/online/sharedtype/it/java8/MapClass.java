package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@SharedType(rustMacroTraits = {"PartialEq", "serde::Serialize", "serde::Deserialize"})
final class MapClass {
    private ConcurrentMap<Integer, String> mapField;
    private Map<EnumSize, String> enumKeyMapField;
    private CustomMap customMapField;
    private Map<String, Map<String, Integer>> nestedMapField;
}

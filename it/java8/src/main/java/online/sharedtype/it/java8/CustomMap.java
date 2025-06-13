package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import java.util.HashMap;

public final class CustomMap extends HashMap<Integer, String> {
    private static final long serialVersionUID = 2346546437868922578L;
}

// the type is treated as a Map type, it should be ignored with a warning
@SharedType
class CustomMapAnnotated extends HashMap<Integer, String> {
    private static final long serialVersionUID = 4576189946614168943L;

    private int unfortunatelyIgnored;
}

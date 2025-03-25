package online.sharedtype.it.java8.types;

import online.sharedtype.SharedType;

class MyType1 {
    String value;
}

class MyType2 {
    int value;
}

// mapping defined in it/sharedtype.properties
@SharedType
class ArbitraryTypeMapping {
    private MyType1 myType1;
    private MyType2 myType2;
}

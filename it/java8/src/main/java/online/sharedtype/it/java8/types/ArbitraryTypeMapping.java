package online.sharedtype.it.java8.types;

import online.sharedtype.SharedType;

@SharedType.Ignore
class MyType1<T> {
    T value;
}

class MyType2 {
    int value;
}

// mapping defined in it/sharedtype.properties
@SharedType
class ArbitraryTypeMapping<T> {
    private MyType1<T> myType1T;
    private MyType1<String> myType1String;
    private MyType2 myType2;
}

// todo: 1. SharedType.Ignore won't raise error.
//  2. update mapped name at earlier stage into domain object to simplify mapping logics in type converters.
//  3. verify global properties to ignore types

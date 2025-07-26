package online.sharedtype.maven.it;

import online.sharedtype.SharedType;

import java.util.Optional;

@SharedType
final class MyClass {
    private int value;
}

@SharedType
final class MyClass2<T extends Optional<T>> {
    private T value;
}

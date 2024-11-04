package org.sharedtype.it.java8;

public abstract class SuperClassA extends IgnoredSuperClassB implements InterfaceA<Integer>, IgnoredInterfaceB {
    private int a;

    @Override
    public Integer getValue() {
        return a;
    }
}

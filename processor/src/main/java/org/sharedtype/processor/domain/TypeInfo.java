package org.sharedtype.processor.domain;

public sealed interface TypeInfo permits ArrayTypeInfo, ConcreteTypeInfo, TypeVariableInfo {
    boolean resolved();
}

package org.sharedtype.processor.domain;

import java.io.Serializable;

public sealed interface TypeInfo extends Serializable permits ArrayTypeInfo, ConcreteTypeInfo, TypeVariableInfo {
    boolean resolved();
}

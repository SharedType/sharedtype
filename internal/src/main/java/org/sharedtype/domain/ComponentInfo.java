package org.sharedtype.domain;

import java.io.Serializable;

public sealed interface ComponentInfo extends Serializable permits EnumValueInfo, FieldComponentInfo {
    boolean resolved();
}

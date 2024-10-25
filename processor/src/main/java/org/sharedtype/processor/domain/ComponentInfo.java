package org.sharedtype.processor.domain;

import java.io.Serializable;

public sealed interface ComponentInfo extends Serializable permits FieldComponentInfo {
}

package org.sharedtype.processor.domain;

import java.io.Serializable;
import java.util.List;

public sealed interface TypeDef extends Serializable permits ClassDef {
    String qualifiedName();

    String name();

    List<? extends ComponentInfo> components();

    /**
     * @return true if all required types are resolved.
     */
    boolean resolved();
}

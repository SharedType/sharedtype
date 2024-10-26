package org.sharedtype.domain;

import java.io.Serializable;
import java.util.List;

public sealed interface TypeDef extends Serializable permits ClassDef, EnumDef {
    String qualifiedName();

    String simpleName();

    List<? extends ComponentInfo> components();

    /**
     * @return true if all required types are resolved.
     */
    boolean resolved();
}

package org.sharedtype.domain;

import java.io.Serializable;
import java.util.List;

public interface TypeDef extends Serializable {
    String qualifiedName();

    String simpleName();

    List<? extends ComponentInfo> components();

    /**
     * @return true if all required types are resolved.
     */
    boolean resolved();
}

package org.sharedtype.it.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
final class DependencyClassC {
    private DependencyClassA a;
}

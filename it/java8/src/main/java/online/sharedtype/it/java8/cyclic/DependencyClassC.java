package online.sharedtype.it.java8.cyclic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class DependencyClassC {
    private DependencyClassA a;
}

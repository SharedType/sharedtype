package online.sharedtype.it.java8;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class DependencyClassB {
    private DependencyClassC c;
}

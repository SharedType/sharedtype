package io.github.cuzfrog.sharedtype.it.java8;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Container<T> {
    private T t;
}

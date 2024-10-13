package com.github.jets.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
final class Container<T> {
    private T t;
}

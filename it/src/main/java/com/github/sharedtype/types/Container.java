package com.github.sharedtype.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
final class Container<T> {
    private T t;
}

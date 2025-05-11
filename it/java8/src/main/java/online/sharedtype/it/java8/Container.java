package online.sharedtype.it.java8;

import lombok.Data;

@Data
public final class Container<T> {
    private T t;
}

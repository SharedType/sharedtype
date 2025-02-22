package online.sharedtype.it.java8;

import lombok.Setter;
import online.sharedtype.SharedType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SharedType
@Setter
public class OptionalMethod {
    @SharedType.Ignore
    private String value;

//    Optional<List<Optional<String>>> getValueOptional() {
//        return Optional.of(Collections.singletonList(Optional.ofNullable(value)));
//    }
}

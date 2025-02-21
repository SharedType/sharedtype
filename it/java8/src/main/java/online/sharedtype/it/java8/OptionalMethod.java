package online.sharedtype.it.java8;

import lombok.Setter;
import online.sharedtype.SharedType;

import java.util.Optional;

@SharedType
@Setter
public class OptionalMethod {
    @SharedType.Ignore
    private String value;

//    Optional<String> getValueOptional() {
//        return Optional.ofNullable(value);
//    }
}

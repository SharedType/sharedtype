package online.sharedtype.it.java8;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Setter;
import online.sharedtype.SharedType;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SharedType(
    rustMacroTraits = {"PartialEq", "serde::Serialize", "serde::Deserialize"},
    typescriptOptionalFieldFormat = {"undefined", "null"}
)
@Setter
public class OptionalMethod {
    @SharedType.Ignore
    private String value;

    @JsonGetter
    Optional<String> getValueOptional() {
        return Optional.ofNullable(value);
    }

    @JsonGetter
    Optional<Optional<String>> getNestedValueOptional() {
        return Optional.of(Optional.ofNullable(value));
    }

    @JsonGetter
    Optional<Set<Optional<String>>> getSetNestedValueOptional() {
        return Optional.of(Collections.singleton(Optional.ofNullable(value)));
    }

    @JsonGetter
    Optional<Map<Integer, Optional<String>>> getMapNestedValueOptional() {
        return Optional.of(Collections.singletonMap(1, Optional.ofNullable(value)));
    }
}

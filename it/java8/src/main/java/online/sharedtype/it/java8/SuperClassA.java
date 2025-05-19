package online.sharedtype.it.java8;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public abstract class SuperClassA extends IgnoredSuperClassB implements InterfaceSubA<Integer>, IgnoredInterfaceB {
    private int a;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Override
    public Integer getValue() {
        return a;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Override
    public int getNotIgnoredImplementedMethod() {
        return 0;
    }
}

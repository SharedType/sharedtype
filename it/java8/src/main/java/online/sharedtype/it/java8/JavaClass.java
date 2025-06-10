package online.sharedtype.it.java8;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import online.sharedtype.SharedType;

@EqualsAndHashCode(callSuper = true)
@Setter
@SharedType(
    rustMacroTraits = {"PartialEq", "Eq", "Hash", "serde::Serialize", "serde::Deserialize"}
)
public final class JavaClass extends SuperClassA {
    static final long SOME_LONG_VALUE = 123L;

    @SharedType.TagLiteral(tags = "// test comments for field")
    @SharedType.TagLiteral(
        tags = "// test inline comments",
        position = SharedType.TagPosition.INLINE_AFTER,
        targets = {SharedType.TargetType.RUST, SharedType.TargetType.TYPESCRIPT}
    )
    @SharedType.TagLiteral(tags = "`json:\"string,omitempty\"` //override tags", position = SharedType.TagPosition.INLINE_AFTER, targets = SharedType.TargetType.GO)
    @Getter
    private String string;
    @Getter
    private EnumSize size;
//    private IgnoredInterfaceB b; // compilation failure
    private @SharedType.Ignore IgnoredInterfaceB ignoredB;

    @Override
    public int getNotIgnoredImplementedMethod() {
        return 1;
    }

    @SharedType
    static class InnerClass {
        private int value;
    }
}

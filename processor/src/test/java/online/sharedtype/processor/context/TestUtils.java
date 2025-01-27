package online.sharedtype.processor.context;

import lombok.experimental.UtilityClass;
import online.sharedtype.SharedType;

@UtilityClass
public final class TestUtils {
    public static SharedType defaultSharedTypeAnnotation() {
        return Config.DummyDefault.class.getAnnotation(Config.AnnoContainer.class).anno();
    }
}

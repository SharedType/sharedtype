package online.sharedtype.processor.context;

import lombok.experimental.UtilityClass;
import online.sharedtype.SharedType;

import static org.mockito.Mockito.spy;

@UtilityClass
public final class TestUtils {
    public static SharedType spiedDefaultSharedTypeAnnotation() {
        return spy(Config.DummyDefault.class.getAnnotation(Config.AnnoContainer.class).anno());
    }

    @SuppressWarnings("unchecked")
    public static <T extends S, S> T typeCast(S s) {
        return (T) s;
    }
}

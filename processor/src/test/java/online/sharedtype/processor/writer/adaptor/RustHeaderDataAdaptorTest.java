package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.ContextMocks;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


final class RustHeaderDataAdaptorTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustHeaderDataAdaptor adaptor = new RustHeaderDataAdaptor(ctxMocks.getContext());

    @Test
    void allowExpr() {
        assertThat(adaptor.allowExpr()).containsPattern("#!\\[allow\\([\\w\\s,]+\\)]");
    }
}

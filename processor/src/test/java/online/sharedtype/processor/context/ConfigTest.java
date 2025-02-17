package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConfigTest {
    private final ContextMocks ctxMocks = new ContextMocks();

    @BeforeEach
    void setup() {
        when(ctxMocks.getContext().getProps().isConstantNamespaced()).thenReturn(true);
    }

    @Test
    void constantInlinedFallbackToGlobalDefault() {
        Config config = new Config(ctxMocks.typeElement("com.github.cuzfrog.Abc").element(), ctxMocks.getContext());
        assertThat(config.isConstantNamespaced()).isTrue();
    }

    @Test
    void constantInlinedOverrideGlobalDefault() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.constantNamespaced()).thenReturn(SharedType.OptionalBool.FALSE))
            .element();

        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.isConstantNamespaced()).isFalse();
    }
}

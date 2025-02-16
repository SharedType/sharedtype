package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConfigTest {
    private final ContextMocks ctxMocks = new ContextMocks();

    @Test
    void typescriptConstantInlinedFallbackToGlobalDefault() {
        Props.Typescript tsConfig = mock(Props.Typescript.class);
        when(ctxMocks.getContext().getProps().getTypescript()).thenReturn(tsConfig);
        when(tsConfig.isConstantInline()).thenReturn(true);

        Config config = new Config(ctxMocks.typeElement("com.github.cuzfrog.Abc").element(), ctxMocks.getContext());
        assertThat(config.isTypescriptConstantInlined()).isTrue();
    }

    @Test
    void typescriptConstantInlinedOverrideGlobalDefault() {
        Props.Typescript tsConfig = mock(Props.Typescript.class);
        when(ctxMocks.getContext().getProps().getTypescript()).thenReturn(tsConfig);
        when(tsConfig.isConstantInline()).thenReturn(true);

        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.typescriptConstantInlined()).thenReturn(SharedType.OptionalBool.FALSE))
            .element();

        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.isTypescriptConstantInlined()).isFalse();
    }
}

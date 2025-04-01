package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.ContextMocks;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;


final class TypescriptHeaderDataAdaptorTest {
    @SetSystemProperty(key = "sharedtype.typescript.custom-code-path", value = "src/test/resources/custom-code.ts")
    @Test
    void readCustomCodeSnippet() {
        ContextMocks ctxMocks = new ContextMocks();
        TypescriptHeaderDataAdaptor adaptor = new TypescriptHeaderDataAdaptor(ctxMocks.getContext());
        assertThat(adaptor.customCodeSnippet()).isEqualTo("interface A {}" + System.lineSeparator());
    }

    @SetSystemProperty(key = "sharedtype.typescript.custom-code-path", value = "not-exists.ts")
    @Test
    void customCodeSnippetNoFile() {
        ContextMocks ctxMocks = new ContextMocks();
        TypescriptHeaderDataAdaptor adaptor = new TypescriptHeaderDataAdaptor(ctxMocks.getContext());
        assertThat(adaptor.customCodeSnippet()).isEqualTo("");
    }
}

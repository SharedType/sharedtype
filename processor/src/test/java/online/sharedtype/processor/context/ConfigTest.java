package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import online.sharedtype.processor.support.exception.SharedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.NULL;
import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.QUESTION_MARK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void parseTypescriptOptionalFieldFormats() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.typescriptOptionalFieldFormat()).thenReturn(new String[]{"?", "null"}))
            .element();

        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getTypescriptOptionalFieldFormats()).containsExactly(QUESTION_MARK, NULL);
    }

    @Test
    void invalidTypescriptOptionalFieldFormat() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.typescriptOptionalFieldFormat()).thenReturn(new String[]{"abc"}))
            .element();

        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("[abc], only '?', 'null', 'undefined' are allowed");
    }

    @Test
    void parseTsEnumFormat() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.typescriptEnumFormat()).thenReturn("const_enum"))
            .element();
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getTypescriptEnumFormat()).isEqualTo(Props.Typescript.EnumFormat.CONST_ENUM);
    }

    @Test
    void invalidTsEnumFormat() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.typescriptEnumFormat()).thenReturn("abc"))
            .element();
        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("Invalid value for SharedType.typescriptEnumFormat: 'abc', only one of 'union', 'const_enum', 'enum' is allowed.");
    }

    @Test
    void parseGoEnumFormat() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.goEnumFormat()).thenReturn("const"))
            .element();
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getGoEnumFormat()).isEqualTo(Props.Go.EnumFormat.CONST);
    }

    @Test
    void invalidGoEnumFormat() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.goEnumFormat()).thenReturn("abc"))
            .element();
        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("Invalid value for SharedType.goEnumFormat: 'abc', only 'const' or 'struct' is allowed.");
    }

    @Test
    void overrideTsFieldReadonly() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.typescriptFieldReadonlyType()).thenReturn("all"))
            .element();
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getTypescriptFieldReadonly()).isEqualTo(Props.Typescript.FieldReadonlyType.ALL);
    }

    @Test
    void overrideRustTargetDatetimeTypeLiteral() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class, m -> when(m.rustTargetDatetimeTypeLiteral()).thenReturn("int64"))
            .element();
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getRustTargetDatetimeTypeLiteral()).isEqualTo("int64");
    }
}

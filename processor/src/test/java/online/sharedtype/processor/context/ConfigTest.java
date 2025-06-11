package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import online.sharedtype.processor.support.exception.SharedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.TypeElement;

import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.NULL;
import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.QUESTION_MARK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

final class ConfigTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final SharedType anno = TestUtils.spiedDefaultSharedTypeAnnotation();
    private final TypeElement typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
        .withAnnotation(SharedType.class, () -> anno)
        .element();

    @BeforeEach
    void setup() {
        when(ctxMocks.getContext().getProps().isConstantNamespaced()).thenReturn(true);
        when(anno.rustConstKeyword()).thenReturn("const");
    }

    @Test
    void constantInlinedFallbackToGlobalDefault() {
        Config config = new Config(ctxMocks.typeElement("com.github.cuzfrog.Abc").element(), ctxMocks.getContext());
        assertThat(config.isConstantNamespaced()).isTrue();
    }

    @Test
    void constantInlinedOverrideGlobalDefault() {
        when(anno.constantNamespaced()).thenReturn(SharedType.OptionalBool.FALSE);
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.isConstantNamespaced()).isFalse();
    }

    @Test
    void parseTypescriptOptionalFieldFormats() {
        when(anno.typescriptOptionalFieldFormat()).thenReturn(new String[]{"?", "null"});
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getTypescriptOptionalFieldFormats()).containsExactly(QUESTION_MARK, NULL);
    }

    @Test
    void invalidTypescriptOptionalFieldFormat() {
        when(anno.typescriptOptionalFieldFormat()).thenReturn(new String[]{"abc"});
        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("[abc], only '?', 'null', 'undefined' are allowed");
    }

    @Test
    void parseTsEnumFormat() {
        when(anno.typescriptEnumFormat()).thenReturn("const_enum");
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getTypescriptEnumFormat()).isEqualTo(Props.Typescript.EnumFormat.CONST_ENUM);
    }

    @Test
    void invalidTsEnumFormat() {
        when(anno.typescriptEnumFormat()).thenReturn("abc");
        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("Invalid value for SharedType.typescriptEnumFormat: 'abc', only one of 'union', 'const_enum', 'enum' is allowed.");
    }

    @Test
    void parseGoEnumFormat() {
        when(anno.goEnumFormat()).thenReturn("const");
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getGoEnumFormat()).isEqualTo(Props.Go.EnumFormat.CONST);
    }

    @Test
    void invalidGoEnumFormat() {
        when(anno.goEnumFormat()).thenReturn("abc");
        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("Invalid value for SharedType.goEnumFormat: 'abc', only 'const' or 'struct' is allowed.");
    }

    @Test
    void overrideTsFieldReadonly() {
        when(anno.typescriptFieldReadonlyType()).thenReturn("all");
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getTypescriptFieldReadonly()).isEqualTo(Props.Typescript.FieldReadonlyType.ALL);
    }

    @Test
    void overrideRustTargetDatetimeTypeLiteral() {
        when(anno.rustTargetDatetimeTypeLiteral()).thenReturn("int64");
        Config config = new Config(typeElement, ctxMocks.getContext());
        assertThat(config.getRustTargetDatetimeTypeLiteral()).isEqualTo("int64");
    }

    @Test
    void invalidRustConstKeyword() {
        when(anno.rustConstKeyword()).thenReturn("abc");
        assertThatThrownBy(() -> new Config(typeElement, ctxMocks.getContext()))
            .isInstanceOf(SharedTypeException.class)
            .hasMessageContaining("Invalid value for SharedType.rustConstKeyword: 'abc', only 'const' or 'static' is allowed.");
    }
}

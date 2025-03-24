package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class ContextTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final ProcessingEnvironment processingEnv = ctxMocks.getProcessingEnv();
    private Context ctx;

    @BeforeAll
    void setUp() {
        when(processingEnv.getMessager()).thenReturn(mock(Messager.class));
    }

    @Test
    void isIgnoredWhenMarkedWithIgnoreAnno() {
        ctx = new Context(processingEnv, Props.builder().build());
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.Ignore.class)
            .element();
        assertThat(ctx.isIgnored(typeElement)).isTrue();
    }

    @Test
    void ignoredField() {
        ctx = new Context(processingEnv, Props.builder().ignoredFieldNames(Set.of("field333")).build());
        var fieldElement = ctxMocks.declaredTypeVariable("field333", ctxMocks.typeElement("java.lang.String").type())
            .withElementKind(ElementKind.FIELD)
            .element();
        assertThat(ctx.isIgnored(fieldElement)).isTrue();
    }

    @Test
    void ignoredTypes() {
        ctx = new Context(processingEnv, Props.builder().ignoredTypeQualifiedNames(Set.of("com.github.cuzfrog.Abc")).build());
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
        assertThat(ctx.isIgnored(typeElement)).isTrue();
    }

    @Test
    void isSubTypeOfAny() {
        ctx = new Context(processingEnv, Props.builder().ignoredTypeQualifiedNames(Set.of("java.lang.Object")).build());
        var typeMirror = ctxMocks.typeElement("com.github.cuzfrog.A1")
            .withSuperTypes(
                ctxMocks.typeElement("com.github.cuzfrog.A").type(),
                ctxMocks.typeElement("com.github.cuzfrog.AA").type(),
                ctxMocks.typeElement("java.lang.Object").type()
            )
            .type();
        assertThat(ctx.isSubtypeOfAny(typeMirror, Set.of("com.github.cuzfrog.A"))).isTrue();
        assertThat(ctx.isSubtypeOfAny(typeMirror, Set.of("com.github.cuzfrog.AA"))).isTrue();
        assertThat(ctx.isSubtypeOfAny(typeMirror, Set.of("com.github.cuzfrog.B"))).isFalse();
        assertThat(ctx.isSubtypeOfAny(typeMirror, Set.of("java.lang.Object"))).isFalse();
    }
}

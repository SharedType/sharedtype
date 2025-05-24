package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import online.sharedtype.processor.support.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
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
    void isIgnoredWhenMarkedWithCustomIgnoreAnno() {
        ctx = new Context(processingEnv, Props.builder().ignoreAnnotations(Set.of("a.b.Ignore")).build());
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotationMirrors(
                ctxMocks.annotationMirror(ctxMocks.typeElement("a.b.Ignore").type()).mocked()
            )
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

    @Test
    void isOptionalAnnotated() {
        ctx = new Context(processingEnv, Props.builder().optionalAnnotations(Set.of(Nullable.class.getCanonicalName())).build());
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(Nullable.class)
            .element();
        assertThat(ctx.isOptionalAnnotated(typeElement)).isTrue();
    }

    @Test
    void isExplicitAccessorAnnotated() {
        ctx = new Context(processingEnv, Props.builder().build());
        var typeElement = ctxMocks.executable("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.Accessor.class)
            .element();
        assertThat(ctx.isExplicitAccessor(typeElement)).isTrue();
    }

    @Test
    void isExplicitAccessorCustomAnnotated() {
        ctx = new Context(processingEnv, Props.builder().accessorAnnotations(Set.of("a.b.Accessor")).build());
        var typeElement = ctxMocks.executable("com.github.cuzfrog.Abc")
            .withAnnotationMirrors(
                ctxMocks.annotationMirror(ctxMocks.typeElement("a.b.Accessor").type()).mocked()
            )
            .element();
        assertThat(ctx.isExplicitAccessor(typeElement)).isTrue();
    }

    @Test
    void isEnumValueAnnotated() {
        ctx = new Context(processingEnv, Props.builder().build());
        var typeElement = ctxMocks.executable("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.EnumValue.class)
            .element();
        assertThat(ctx.isAnnotatedAsEnumValue(typeElement)).isTrue();
    }

    @Test
    void isEnumValueCustomAnnotated() {
        ctx = new Context(processingEnv, Props.builder().enumValueAnnotations(Set.of("a.b.EnumValue")).build());
        var typeElement = ctxMocks.executable("com.github.cuzfrog.Abc")
            .withAnnotationMirrors(
                ctxMocks.annotationMirror(ctxMocks.typeElement("a.b.EnumValue").type()).mocked()
            )
            .element();
        assertThat(ctx.isAnnotatedAsEnumValue(typeElement)).isTrue();
    }

    @Test
    void tagLiteralsWithTargets() {
        ctx = new Context(processingEnv, Props.builder().build());
        var variableElement = ctxMocks.executable("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.TagLiteral.class, mock -> {
                when(mock.tags()).thenReturn(new String[]{"a", "b"});
                when(mock.targets()).thenReturn(new SharedType.TargetType[]{SharedType.TargetType.GO, SharedType.TargetType.RUST});
            })
            .withAnnotation(SharedType.TagLiteral.class, mock -> {
                when(mock.tags()).thenReturn(new String[]{"c"});
                when(mock.targets()).thenReturn(new SharedType.TargetType[]{SharedType.TargetType.TYPESCRIPT, SharedType.TargetType.RUST});
            })
            .element();
        var res = ctx.extractTagLiterals(variableElement);
        assertThat(res).hasSize(3);
        assertThat(res.get(SharedType.TargetType.TYPESCRIPT)).isEqualTo(List.of("c"));
        assertThat(res.get(SharedType.TargetType.GO)).isEqualTo(List.of("a", "b"));
        assertThat(res.get(SharedType.TargetType.RUST)).isEqualTo(List.of("a", "b", "c"));
    }

    @Test
    void tagLiteralsWithoutTargetsFallbackToGlobalTargets() {
        ctx = new Context(processingEnv, Props.builder().targetTypes(Set.of(SharedType.TargetType.RUST)).build());
        var variableElement = ctxMocks.executable("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.TagLiteral.class, mock -> {
                when(mock.tags()).thenReturn(new String[]{"a", "b"});
            })
            .element();
        var res = ctx.extractTagLiterals(variableElement);
        assertThat(res).hasSize(1);
        assertThat(res.get(SharedType.TargetType.RUST)).isEqualTo(List.of("a", "b"));
    }
}

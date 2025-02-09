package online.sharedtype.processor.parser;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
final class CompositeTypeDefParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private @Mock TypeDefParser delegate1;
    private @Mock TypeDefParser delegate2;
    private CompositeTypeDefParser parser;

    private final TypeElement typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
    private final ClassDef classDef = ClassDef.builder().simpleName("Abc").build();
    private final ConstantNamespaceDef constDef = ConstantNamespaceDef.builder().simpleName("Abc").build();

    @BeforeEach
    void setUp() {
        parser = new CompositeTypeDefParser(ctxMocks.getContext(), List.of(delegate1, delegate2));
        when(ctxMocks.getTypeStore().getTypeDefs("com.github.cuzfrog.Abc")).thenReturn(null);
    }

    @Test
    void callAllParsers() {
        when(delegate1.parse(typeElement)).thenReturn(Collections.singletonList(classDef));
        when(delegate2.parse(typeElement)).thenReturn(Collections.singletonList(constDef));

        when(typeElement.getKind()).thenReturn(ElementKind.CLASS);

        var inOrder = Mockito.inOrder(delegate1, delegate2, ctxMocks.getContext().getTypeStore());

        var typeDefs = parser.parse(typeElement);
        var typeDef1 = typeDefs.get(0);
        inOrder.verify(delegate1).parse(typeElement);
        assertThat(typeDef1).isEqualTo(classDef);
        assertThat(classDef.isAnnotated()).isFalse();
        inOrder.verify(ctxMocks.getContext().getTypeStore()).saveTypeDef("com.github.cuzfrog.Abc", classDef);

        var typeDef2 = typeDefs.get(1);
        inOrder.verify(delegate2).parse(typeElement);
        assertThat(typeDef2).isEqualTo(constDef);
        inOrder.verify(ctxMocks.getContext().getTypeStore()).saveTypeDef("com.github.cuzfrog.Abc", constDef);
    }

    @Test
    void markClassDefAsAnnotated() {
        var clazz = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withAnnotation(SharedType.class)
            .element();
        when(delegate1.parse(clazz)).thenReturn(Collections.singletonList(classDef));

        var classDef = (ClassDef) parser.parse(clazz).get(0);
        assertThat(classDef.isAnnotated()).isTrue();
    }

    @Test
    void skipOnEmptyResult() {
        when(delegate1.parse(typeElement)).thenReturn(Collections.emptyList());
        assertThat(parser.parse(typeElement)).isEmpty();
        verify(ctxMocks.getTypeStore(), never()).saveTypeDef(any(), any());
    }

    @Test
    void useCachedTypeDef() {
        var typeDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Abc").build();
        var typeDef2 = ConstantNamespaceDef.builder().qualifiedName("com.github.cuzfrog.Abc").build();
        when(ctxMocks.getTypeStore().getTypeDefs("com.github.cuzfrog.Abc")).thenReturn(List.of(typeDef, typeDef2));

        assertThat(parser.parse(typeElement)).containsExactly(typeDef, typeDef2);
        verify(delegate1, never()).parse(any());
        verify(delegate2, never()).parse(any());
    }

    @Test
    void ignoreType() {
        when(ctxMocks.getContext().isIgnored(typeElement)).thenReturn(true);

        assertThat(parser.parse(typeElement)).isEmpty();
        verify(delegate1, never()).parse(any());
        verify(delegate2, never()).parse(any());
    }
}

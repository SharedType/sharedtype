package org.sharedtype.processor.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.domain.ClassDef;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Map;

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
    private final ClassDef classDef = ClassDef.builder().build();

    @BeforeEach
    void setUp() {
        parser = new CompositeTypeDefParser(
            ctxMocks.getContext(),
            Map.of(
                ElementKind.RECORD.name(), delegate1,
                ElementKind.ENUM.name(), delegate2
        ));
        when(ctxMocks.getTypeStore().getTypeDef("com.github.cuzfrog.Abc")).thenReturn(null);
    }

    @Test
    void resolveDelegateParser() {
        when(delegate1.parse(typeElement)).thenReturn(classDef);
        when(delegate2.parse(typeElement)).thenReturn(classDef);
        when(typeElement.getKind()).thenReturn(ElementKind.RECORD);

        var inOrder = Mockito.inOrder(delegate1, delegate2, ctxMocks.getContext().getTypeStore());

        var typeDef = parser.parse(typeElement);
        verify(delegate1).parse(typeElement);
        assertThat(typeDef).isEqualTo(classDef);

        when(typeElement.getKind()).thenReturn(ElementKind.ENUM);
        typeDef = parser.parse(typeElement);
        verify(delegate2).parse(typeElement);
        assertThat(typeDef).isEqualTo(classDef);

        when(typeElement.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        assertThatThrownBy(() -> parser.parse(typeElement));

        inOrder.verify(ctxMocks.getContext().getTypeStore()).saveTypeDef("com.github.cuzfrog.Abc", classDef);
    }

    @Test
    void useCachedTypeDef() {
        var typeDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Abc").build();
        when(ctxMocks.getTypeStore().getTypeDef("com.github.cuzfrog.Abc")).thenReturn(typeDef);

        assertThat(parser.parse(typeElement)).isSameAs(typeDef);
        verify(delegate1, never()).parse(any());
        verify(delegate2, never()).parse(any());
    }

    @Test
    void ignoreType() {
        when(ctxMocks.getContext().isTypeIgnored(typeElement)).thenReturn(true);

        assertThat(parser.parse(typeElement)).isNull();
        verify(delegate1, never()).parse(any());
        verify(delegate2, never()).parse(any());
    }
}

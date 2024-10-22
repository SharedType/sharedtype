package org.sharedtype.processor.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.ClassDef;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class CompositeTypeDefParserTest {
    private @Mock TypeDefParser delegate1;
    private @Mock TypeDefParser delegate2;
    private CompositeTypeDefParser parser;

    private @Mock TypeElement typeElement;
    private final Context ctx = mock(Context.class);
    private final ClassDef classDef = ClassDef.builder().build();

    @BeforeEach
    void setUp() {
        parser = new CompositeTypeDefParser(
            ctx, 
            Map.of(
                ElementKind.RECORD, delegate1,
                ElementKind.ENUM, delegate2
        ));
        when(delegate1.parse(typeElement)).thenReturn(classDef);
        when(delegate2.parse(typeElement)).thenReturn(classDef);
    }

    @Test
    void parse() {
        when(typeElement.getKind()).thenReturn(ElementKind.RECORD);
        var typeDef = parser.parse(typeElement);
        verify(delegate1).parse(typeElement);
        assertThat(typeDef).isEqualTo(classDef);

        when(typeElement.getKind()).thenReturn(ElementKind.ENUM);
        typeDef = parser.parse(typeElement);
        verify(delegate2).parse(typeElement);
        assertThat(typeDef).isEqualTo(classDef);

        when(typeElement.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        assertThatThrownBy(() -> parser.parse(typeElement));
    }
}

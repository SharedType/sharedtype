package org.jets.processor.parser;

import org.jets.processor.JetsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class CompositeTypeElementParserTest {
    private @Mock TypeElementParser delegate1;
    private @Mock TypeElementParser delegate2;
    private CompositeTypeElementParser parser;

    private @Mock TypeElement typeElement;
    private final JetsContext ctx = JetsContext.builder().build();
    private final TypeInfo typeInfo = TypeInfo.builder().build();

    @BeforeEach
    void setUp() {
        parser = new CompositeTypeElementParser(Map.of(
                ElementKind.RECORD, delegate1,
                ElementKind.ENUM, delegate2
        ));
        when(delegate1.parse(typeElement, ctx)).thenReturn(typeInfo);
        when(delegate2.parse(typeElement, ctx)).thenReturn(typeInfo);
    }

    @Test
    void parse() {
        when(typeElement.getKind()).thenReturn(ElementKind.RECORD);
        var info = parser.parse(typeElement, ctx);
        verify(delegate1).parse(typeElement, ctx);
        assertThat(info).isEqualTo(typeInfo);

        when(typeElement.getKind()).thenReturn(ElementKind.ENUM);
        info = parser.parse(typeElement, ctx);
        verify(delegate2).parse(typeElement, ctx);
        assertThat(info).isEqualTo(typeInfo);


        when(typeElement.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        info = parser.parse(typeElement, ctx);
        assertThat(info).isNull();
    }
}
package org.jets.processor.parser;

import java.util.Map;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.jets.processor.GlobalContext;
import org.jets.processor.domain.ClassInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class CompositeTypeElementParserTest {
    private @Mock TypeElementParser delegate1;
    private @Mock TypeElementParser delegate2;
    private CompositeTypeElementParser parser;

    private @Mock TypeElement typeElement;
    private final GlobalContext ctx = new GlobalContext(null, null);
    private final ClassInfo typeInfo = ClassInfo.builder().build();

    @BeforeEach
    void setUp() {
        parser = new CompositeTypeElementParser(
            ctx, 
            Map.of(
                ElementKind.RECORD, delegate1,
                ElementKind.ENUM, delegate2
        ));
        when(delegate1.parse(typeElement)).thenReturn(typeInfo);
        when(delegate2.parse(typeElement)).thenReturn(typeInfo);
    }

    @Test
    void parse() {
        when(typeElement.getKind()).thenReturn(ElementKind.RECORD);
        var info = parser.parse(typeElement);
        verify(delegate1).parse(typeElement);
        assertThat(info).isEqualTo(typeInfo);

        when(typeElement.getKind()).thenReturn(ElementKind.ENUM);
        info = parser.parse(typeElement);
        verify(delegate2).parse(typeElement);
        assertThat(info).isEqualTo(typeInfo);


        when(typeElement.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        info = parser.parse(typeElement);
        assertThat(info).isNull();
    }
}
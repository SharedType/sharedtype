package org.sharedtype.processor.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sharedtype.annotation.SharedType;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.context.ExecutableElementMock;
import org.sharedtype.processor.context.TypeElementMock;
import org.sharedtype.processor.context.DeclaredTypeVariableElementMock;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class TypeDefParserResolveComponentsTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final TypeDefParserImpl parser = new TypeDefParserImpl(ctxMocks.getContext(), typeInfoParser);

    private final Config config = mock(Config.class);
    private final TypeElementMock string = ctxMocks.typeElement("java.lang.String");
    private final DeclaredTypeVariableElementMock field1 = ctxMocks
        .declaredTypeVariable("value", string.type())
        .withElementKind(ElementKind.FIELD);
    private final ExecutableElementMock method1 = ctxMocks.executable("value")
        .withElementKind(ElementKind.METHOD)
        .withReturnType(string.type());
    private final ExecutableElementMock method2 = ctxMocks.executable("value2")
        .withElementKind(ElementKind.METHOD)
        .withReturnType(string.type());
    private final TypeElement recordElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
      .withElementKind(ElementKind.RECORD)
      .withEnclosedElements(
        field1.element(),
        method1.element(),
        method2.element()
      )
      .element();

    @BeforeEach
    void setUp() {
        when(config.includes(any())).thenReturn(true);
        when(ctxMocks.getTypes().isSameType(string.type(), string.type())).thenReturn(true);
    }

    @Test
    void deduplicateFieldAndAccessor() {
        var components = parser.resolveComponents(recordElement, config);
        assertThat(components).containsExactly(field1.element(), method2.element());
        verify(ctxMocks.getContext(), never()).error(any(), any(Object[].class));
    }

    @Test
    void resolveField() {
        when(config.includes(SharedType.ComponentType.ACCESSORS)).thenReturn(false);
        var components = parser.resolveComponents(recordElement, config);
        assertThat(components).containsExactly(field1.element());
    }

    @Test
    void resolveAccessor() {
        when(config.includes(SharedType.ComponentType.FIELDS)).thenReturn(false);
        var components = parser.resolveComponents(recordElement, config);
        assertThat(components).containsExactly(method1.element(), method2.element());
    }
}

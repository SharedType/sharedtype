package org.sharedtype.processor.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sharedtype.annotation.SharedType;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.context.ExecutableElementMock;
import org.sharedtype.processor.context.VariableElementMock;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TypeDefParserResolveComponentsTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final TypeDefParserImpl parser = new TypeDefParserImpl(ctxMocks.getContext(), typeInfoParser);

    private final Config config = mock(Config.class);
    private final VariableElementMock<DeclaredType> field1 = ctxMocks.variableElement("value", DeclaredType.class).withElementKind(ElementKind.FIELD);
    private final ExecutableElementMock method1 = ctxMocks.executableElement("value").withElementKind(ElementKind.METHOD);
    private final ExecutableElementMock method2 = ctxMocks.executableElement("value2").withElementKind(ElementKind.METHOD);
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
    }

    @Test
    void deduplicateFieldAndAccessor() {
        var components = parser.resolveComponents(recordElement, config);
        assertThat(components).containsExactly(field1.element(), method2.element());
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

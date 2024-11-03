package org.sharedtype.processor.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sharedtype.annotation.SharedType;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.context.DeclaredTypeVariableElementMock;
import org.sharedtype.processor.context.ExecutableElementMock;
import org.sharedtype.processor.context.RecordComponentMock;
import org.sharedtype.processor.context.TypeElementMock;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.NestingKind;
import javax.lang.model.type.DeclaredType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ClassTypeDefParserForRecordTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final ClassTypeDefParser parser = new ClassTypeDefParser(ctxMocks.getContext(), typeInfoParser);

    private final Config config = mock(Config.class);
    private final TypeElementMock string = ctxMocks.typeElement("java.lang.String");
    private final DeclaredTypeVariableElementMock field1 = ctxMocks
        .declaredTypeVariable("value", string.type())
        .withElementKind(ElementKind.FIELD);
    private final ExecutableElementMock method1 = ctxMocks.executable("value")
        .withElementKind(ElementKind.METHOD)
        .withReturnType(string.type());
    private final RecordComponentMock<DeclaredType> recordComponent1 = ctxMocks
        .recordComponent("value", string.type())
        .withAccessor(method1.element());
    private final ExecutableElementMock method1get = ctxMocks.executable("getValue")
        .withElementKind(ElementKind.METHOD)
        .withReturnType(string.type());
    private final ExecutableElementMock method2 = ctxMocks.executable("getValue2")
        .withElementKind(ElementKind.METHOD)
        .withReturnType(string.type())
        .withAnnotation(SharedType.Accessor.class);
    private final TypeElementMock recordElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
        .withElementKind(ElementKind.RECORD)
        .withEnclosedElements(
            field1.element(),
            method1get.element(),
            method1.element(),
            method2.element()
        )
        .withRecordComponentElements(
            recordComponent1.element()
        );

    @BeforeEach
    void setUp() {
        when(config.includes(any())).thenReturn(true);
        when(ctxMocks.getTypes().isSameType(string.type(), string.type())).thenReturn(true);
    }

    @Test
    void resolveFieldsAndAccessors() {
        var components = parser.resolveComponents(recordElement.element(), config);
        assertThat(components).hasSize(2);

        var component1 = components.get(0);
        assertThat(component1.a()).isEqualTo(field1.element());
        assertThat(component1.b()).isEqualTo("value");

        var component2 = components.get(1);
        assertThat(component2.a()).isEqualTo(method2.element());
        assertThat(component2.b()).isEqualTo("value2");

        verify(ctxMocks.getContext(), never()).error(any(), any(Object[].class));
    }

    @Test
    void resolveFields() {
        when(config.includes(SharedType.ComponentType.ACCESSORS)).thenReturn(false);
        var components = parser.resolveComponents(recordElement.element(), config);
        assertThat(components).satisfiesExactly(component -> {
            assertThat(component.a()).describedAs("element").isEqualTo(field1.element());
            assertThat(component.b()).describedAs("name").isEqualTo("value");
        });
    }

    @Test
    void resolveAccessors() {
        when(config.includes(SharedType.ComponentType.FIELDS)).thenReturn(false);
        var components = parser.resolveComponents(recordElement.element(), config);
        assertThat(components).satisfiesExactly(
            component1 -> {
                assertThat(component1.a()).describedAs("element").isEqualTo(method1get.element());
                assertThat(component1.b()).describedAs("name").isEqualTo("value");
            },
            component2 -> {
                assertThat(component2.a()).describedAs("element").isEqualTo(method2.element());
                assertThat(component2.b()).describedAs("name").isEqualTo("value2");
            }
        );
    }

    @Test
    void nonStaticInnerClassIsInvalid() {
        var typeDef = parser.parse(recordElement.withModifiers().withNestingKind(NestingKind.MEMBER).element());
        assertThat(typeDef).isNull();

        verify(ctxMocks.getContext()).error(any(), any(Object[].class));
    }
}

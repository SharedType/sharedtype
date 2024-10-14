package org.jets.processor.parser.field;

import org.assertj.core.api.SoftAssertions;
import org.jets.processor.domain.ConcreteTypeInfo;
import org.jets.processor.parser.context.ContextMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypescriptVariableElementParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptVariableElementParser parser = new TypescriptVariableElementParser(ctxMocks.getContext());

    @BeforeEach
    void setUp() {
    }

    @ParameterizedTest
    @CsvSource({
            "BYTE, number",
            "CHAR, string",
            "DOUBLE, number",
            "FLOAT, number",
            "INT, number",
            "LONG, number",
            "SHORT, number",
            "BOOLEAN, boolean"
    })
    void parsePrimitives(TypeKind typeKind, String expectedSimpleName) {
        var element = mock(VariableElement.class);
        var typeMirror = mock(TypeMirror.class);
        when(element.asType()).thenReturn(typeMirror);
        when(typeMirror.getKind()).thenReturn(typeKind);

        var typeInfo = (ConcreteTypeInfo)parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedSimpleName);
            softly.assertThat(typeInfo.resolved()).isTrue();
        });
    }

    @ParameterizedTest
    @CsvSource({
            "java.lang.Boolean, boolean",
            "java.lang.Byte, number",
            "java.lang.Character, string",
            "java.lang.Double, number",
            "java.lang.Float, number",
            "java.lang.Integer, number",
            "java.lang.Long, number",
            "java.lang.Short, number",
            "java.lang.String, string",
            "java.lang.Void, never",
            "java.lang.Object, any"
    })
    void parsePredefinedObject(String objectName, String expectedSimpleName) {
        var element = mock(VariableElement.class);
        var typeMirror = mock(DeclaredType.class);
        when(element.asType()).thenReturn(typeMirror);
        when(typeMirror.getKind()).thenReturn(TypeKind.DECLARED);

        var typeElement = mock(TypeElement.class);
        when(typeMirror.asElement()).thenReturn(typeElement);
        var typeElementName = mock(Name.class);
        when(typeElement.getQualifiedName()).thenReturn(typeElementName);
        when(typeElementName.toString()).thenReturn(objectName);

        var typeInfo = (ConcreteTypeInfo)parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(objectName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedSimpleName);
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.isArray()).isFalse();
        });
    }
}

package org.jets.processor.parser.field;

import org.assertj.core.api.SoftAssertions;
import org.jets.processor.domain.ConcreteTypeInfo;
import org.jets.processor.parser.context.ContextMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import static org.mockito.ArgumentMatchers.any;
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
        var element = ctxMocks.typeMockBuilder(VariableElement.class).withTypeKind(typeKind).build();

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
        var element = ctxMocks.typeMockBuilder(VariableElement.class, DeclaredType.class)
                .withTypeKind(TypeKind.DECLARED).withTypeElementQualifiedName(objectName).build();

        var typeInfo = (ConcreteTypeInfo)parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(objectName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedSimpleName);
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.isArray()).isFalse();
        });
    }

    @Test @MockitoSettings(strictness = Strictness.LENIENT)
    void parseArraylikeObject() {
        var element = ctxMocks.typeMockBuilder(VariableElement.class, DeclaredType.class)
                .withTypeKind(TypeKind.DECLARED)
                .withTypeElementQualifiedName("java.util.List")
                .withTypeArgumentQualifiedNames("java.lang.String")
                .build();
        when(ctxMocks.getExtraUtils().isArraylike(any())).thenReturn(true);

        var typeInfo = (ConcreteTypeInfo)parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            softly.assertThat(typeInfo.simpleName()).isNull();
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.isArray()).isTrue();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }
}

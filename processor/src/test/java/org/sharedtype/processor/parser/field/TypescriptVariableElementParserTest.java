package org.sharedtype.processor.parser.field;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.domain.ConcreteTypeInfo;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypescriptVariableElementParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptVariableElementParser parser = new TypescriptVariableElementParser(ctxMocks.getContext());
    private final TypeElement stringTypeElement = ctxMocks.buildTypeElement("java.lang.String").element();
    private final TypeElement abcTypeElem = ctxMocks.buildTypeElement("com.github.cuzfrog.Abc").element();

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
        var element = ctxMocks.buildVariableElement(PrimitiveType.class).withTypeKind(typeKind).element();

        var typeInfo = (ConcreteTypeInfo) parser.parse(element);
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
    @MockitoSettings(strictness = Strictness.LENIENT)
    void parsePredefinedObject(String objectName, String expectedSimpleName) {
        var typeElement = ctxMocks.buildTypeElement(objectName).element();
        var element = ctxMocks.buildVariableElement(DeclaredType.class)
                .withTypeKind(TypeKind.DECLARED).withTypeElement(typeElement).element();

        var typeInfo = (ConcreteTypeInfo) parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(objectName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedSimpleName);
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.isArray()).isFalse();
        });
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void parseArraylikeObject() {
        var element = ctxMocks.buildVariableElement(DeclaredType.class)
                .withTypeKind(TypeKind.DECLARED)
                .withTypeElement(
                        ctxMocks.buildTypeElement("java.util.List")
                                .withTypeArguments(stringTypeElement).element()
                )
                .element();
        when(ctxMocks.getContext().isArraylike(any())).thenReturn(true);

        var typeInfo = (ConcreteTypeInfo) parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            softly.assertThat(typeInfo.simpleName()).isEqualTo("string");
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.isArray()).isTrue();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseObject() {
        var element = ctxMocks.buildVariableElement(DeclaredType.class)
                .withTypeKind(TypeKind.DECLARED)
                .withTypeElement(abcTypeElem)
                .element();

        var typeInfo = (ConcreteTypeInfo) parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
            softly.assertThat(typeInfo.simpleName()).isNull();
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.isArray()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseGenericObject() {
        var element = ctxMocks.buildVariableElement(DeclaredType.class)
                .withTypeKind(TypeKind.DECLARED)
                .withTypeElement(
                        ctxMocks.buildTypeElement("com.github.cuzfrog.Container")
                                .withTypeArguments(stringTypeElement, abcTypeElem)
                                .element()
                )
                .element();

        var typeInfo = (ConcreteTypeInfo) parser.parse(element);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Container");
            softly.assertThat(typeInfo.simpleName()).isNull();
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.isArray()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).map(t -> (ConcreteTypeInfo) t).satisfiesExactly(
                    typeArg -> {
                        softly.assertThat(typeArg.qualifiedName()).isEqualTo("java.lang.String");
                        softly.assertThat(typeArg.simpleName()).isEqualTo("string");
                        softly.assertThat(typeArg.resolved()).isTrue();
                        softly.assertThat(typeArg.isArray()).isFalse();
                        softly.assertThat(typeArg.typeArgs()).isEmpty();
                    },
                    typeArg -> {
                        softly.assertThat(typeArg.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
                        softly.assertThat(typeArg.simpleName()).isNull();
                        softly.assertThat(typeArg.resolved()).isFalse();
                        softly.assertThat(typeArg.isArray()).isFalse();
                        softly.assertThat(typeArg.typeArgs()).isEmpty();
                    }
            );
        });
    }
}

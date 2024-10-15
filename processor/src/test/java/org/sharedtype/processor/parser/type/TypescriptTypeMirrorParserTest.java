package org.sharedtype.processor.parser.type;

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
import org.sharedtype.processor.domain.TypeVariableInfo;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TypescriptTypeMirrorParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptTypeMirrorParser parser = new TypescriptTypeMirrorParser(ctxMocks.getContext());

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
        var type = ctxMocks.variableElement(PrimitiveType.class).withTypeKind(typeKind).type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
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
        var type = ctxMocks.variableElement(DeclaredType.class)
          .withTypeKind(TypeKind.DECLARED)
          .withTypeElement(ctxMocks.typeElement(objectName).element())
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(objectName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedSimpleName);
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.isArray()).isFalse();
        });
    }

    @Test
    void parseArraylikeObject() {
        var type = ctxMocks.variableElement(DeclaredType.class)
          .withTypeKind(TypeKind.DECLARED)
          .withTypeElement(
            ctxMocks.typeElement("java.util.List").element()
          )
          .withTypeArguments(ctxMocks.typeElement("java.lang.String").type())
          .type();
        when(ctxMocks.getContext().isArraylike(any())).thenReturn(true);

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
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
        var type = ctxMocks.variableElement(DeclaredType.class)
          .withTypeKind(TypeKind.DECLARED)
          .withTypeElement(ctxMocks.typeElement("com.github.cuzfrog.Abc").element())
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
            softly.assertThat(typeInfo.simpleName()).isNull();
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.isArray()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseGenericObjectWithKnownTypeArgs() {
        var type = ctxMocks.variableElement(DeclaredType.class)
          .withTypeKind(TypeKind.DECLARED)
          .withTypeElement(
            ctxMocks.typeElement("com.github.cuzfrog.Tuple").element()
          )
          .withTypeArguments(
            ctxMocks.typeElement("java.lang.String").type(),
            ctxMocks.typeElement("com.github.cuzfrog.Abc").type()
          )
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Tuple");
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

    @Test
    void parseGenericObjectWithTypeVar() {
        var type = ctxMocks.variableElement(DeclaredType.class)
          .withTypeKind(TypeKind.DECLARED)
          .withTypeElement(
            ctxMocks.typeElement("com.github.cuzfrog.Container").element()
          )
          .withTypeArguments(
            ctxMocks.typeParameterElement("T").type()
          )
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Container");
            softly.assertThat(typeInfo.simpleName()).isNull();
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.isArray()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).map(t -> (TypeVariableInfo) t).satisfiesExactly(
              typeArg -> {
                  softly.assertThat(typeArg.getName()).isEqualTo("T");
                  softly.assertThat(typeArg.resolved()).isTrue();
                  softly.assertThat(typeArg.isArray()).isFalse();
              }
            );
        });
    }
}

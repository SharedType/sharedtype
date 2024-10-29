package org.sharedtype.processor.parser.type;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.domain.ArrayTypeInfo;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.TypeVariableInfo;

import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TypeInfoParserImplTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParserImpl parser = new TypeInfoParserImpl(ctxMocks.getContext());

    @ParameterizedTest
    @CsvSource({
      "BYTE, byte",
      "CHAR, char",
      "DOUBLE, double",
      "FLOAT, float",
      "INT, int",
      "LONG, long",
      "SHORT, short",
      "BOOLEAN, boolean",
    })
    void parsePrimitives(TypeKind typeKind, String expectedName) {
        var type = ctxMocks.primitiveVariable("field1", typeKind).type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(expectedName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedName);
            softly.assertThat(typeInfo.resolved()).isTrue();
        });

        var arrayType = ctxMocks.array(type).type();
        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(arrayType);
        SoftAssertions.assertSoftly(softly -> {
            var componentTypeInfo = (ConcreteTypeInfo) arrayTypeInfo.component();
            softly.assertThat(componentTypeInfo.qualifiedName()).isEqualTo(expectedName);
            softly.assertThat(componentTypeInfo.resolved()).isTrue();
        });
    }

    @ParameterizedTest
    @CsvSource({
      "java.lang.Boolean",
      "java.lang.Byte",
      "java.lang.Character",
      "java.lang.Double",
      "java.lang.Float",
      "java.lang.Integer",
      "java.lang.Long",
      "java.lang.Short",
      "java.lang.String",
      "java.lang.Void",
      "java.lang.Object"
    })
    void parsePredefinedObject(String qualifiedName) {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement(qualifiedName).type())
          .withTypeKind(TypeKind.DECLARED)
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(qualifiedName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1));
            softly.assertThat(typeInfo.resolved()).isTrue();
        });

        var arrayType = ctxMocks.array(type).type();
        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(arrayType);
        SoftAssertions.assertSoftly(softly -> {
            var componentTypeInfo = (ConcreteTypeInfo) arrayTypeInfo.component();
            softly.assertThat(componentTypeInfo.resolved()).isTrue();
        });
    }

    @Test
    void parseArraylikeObject() {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("java.util.List").type())
          .withTypeKind(TypeKind.DECLARED)
          .withTypeArguments(ctxMocks.typeElement("java.lang.String").type())
          .type();
        when(ctxMocks.getContext().isArraylike(type)).thenReturn(true);

        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(type);
        var typeInfo = (ConcreteTypeInfo) arrayTypeInfo.component();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            softly.assertThat(typeInfo.simpleName()).isEqualTo("String");
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseNestedArrays() {
        var nestedType = ctxMocks.typeElement("java.lang.Set")
          .withTypeArguments(
            ctxMocks.typeElement("java.lang.String").type()
          )
          .type();
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("java.util.List").type())
          .withTypeKind(TypeKind.DECLARED)
          .withTypeArguments(
            nestedType
          )
          .type();
        when(ctxMocks.getContext().isArraylike(type)).thenReturn(true);
        when(ctxMocks.getContext().isArraylike(nestedType)).thenReturn(true);

        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(type);
        var nestedArrayTypeInfo = (ArrayTypeInfo) arrayTypeInfo.component();
        var typeInfo = (ConcreteTypeInfo) nestedArrayTypeInfo.component();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            softly.assertThat(typeInfo.simpleName()).isEqualTo("String");
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseObject() {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("com.github.cuzfrog.Abc").type())
          .withTypeKind(TypeKind.DECLARED)
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
            softly.assertThat(typeInfo.simpleName()).isEqualTo("Abc");
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseGenericObjectWithKnownTypeArgs() {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("com.github.cuzfrog.Tuple").type())
          .withTypeKind(TypeKind.DECLARED)
          .withTypeArguments(
            ctxMocks.typeElement("java.lang.String").type(),
            ctxMocks.typeElement("com.github.cuzfrog.Abc").type()
          )
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Tuple");
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).map(t -> (ConcreteTypeInfo) t).satisfiesExactly(
              typeArg -> {
                  softly.assertThat(typeArg.qualifiedName()).isEqualTo("java.lang.String");
                  softly.assertThat(typeArg.simpleName()).isEqualTo("String");
                  softly.assertThat(typeArg.resolved()).isTrue();
                  softly.assertThat(typeArg.typeArgs()).isEmpty();
              },
              typeArg -> {
                  softly.assertThat(typeArg.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
                  softly.assertThat(typeArg.simpleName()).isEqualTo("Abc");
                  softly.assertThat(typeArg.resolved()).isFalse();
                  softly.assertThat(typeArg.typeArgs()).isEmpty();
              }
            );
        });
    }

    @Test
    void parseGenericObjectWithTypeVar() {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("com.github.cuzfrog.Container").type())
          .withTypeKind(TypeKind.DECLARED)
          .withTypeArguments(
            ctxMocks.typeParameter("T").type()
          )
          .type();

        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Container");
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).map(t -> (TypeVariableInfo) t).satisfiesExactly(
              typeArg -> {
                  softly.assertThat(typeArg.getName()).isEqualTo("T");
                  softly.assertThat(typeArg.resolved()).isTrue();
              }
            );
        });
    }

    @Test
    void parseMethod() {
        var type = ctxMocks.executable("value")
            .withReturnType(ctxMocks.typeElement("java.lang.String").type())
            .type();
        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void reuseDeclaredTypeInfoFromCache() {
        var type = ctxMocks.typeElement("com.github.cuzfrog.Abc").type();
        var cachedTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.Abc")
            .resolved(false)
            .build();
        ctxMocks.getTypeCache().saveTypeInfo("com.github.cuzfrog.Abc", cachedTypeInfo);
        var typeInfo = (ConcreteTypeInfo) parser.parse(type);
        assertThat(typeInfo).isSameAs(cachedTypeInfo);
    }
}

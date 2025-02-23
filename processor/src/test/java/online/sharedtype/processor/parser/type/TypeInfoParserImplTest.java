package online.sharedtype.processor.parser.type;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.DependingKind;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.Issue;

import javax.lang.model.type.TypeKind;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TypeInfoParserImplTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParserImpl parser = new TypeInfoParserImpl(ctxMocks.getContext());

    private final TypeContext typeContext = TypeContext.builder()
        .typeDef(ClassDef.builder().qualifiedName("com.github.cuzfrog.Abc").build())
        .dependingKind(DependingKind.COMPONENTS).build();
    private final TypeContext typeContextOuter = TypeContext.builder()
        .typeDef(ClassDef.builder().qualifiedName("com.github.cuzfrog.Outer").build())
        .dependingKind(DependingKind.COMPONENTS).build();

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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContext);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(expectedName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedName);
            softly.assertThat(typeInfo.resolved()).isTrue();
        });

        var arrayType = ctxMocks.array(type).type();
        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(arrayType, typeContext);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContext);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(qualifiedName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1));
            softly.assertThat(typeInfo.resolved()).isTrue();
        });

        var arrayType = ctxMocks.array(type).type();
        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(arrayType, typeContext);
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

        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(type, typeContext);
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

        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(type, typeContext);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContextOuter);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
            softly.assertThat(typeInfo.simpleName()).isEqualTo("Abc");
            softly.assertThat(typeInfo.referencingTypes()).contains(typeContextOuter.getTypeDef());
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @ParameterizedTest
    @CsvSource({
        "false, false",
        "true, false",
        "false, true",
    })
    void setTypeFlags(boolean isEnum, boolean isMaplike) {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("com.github.cuzfrog.SomeType").type())
            .withTypeKind(TypeKind.DECLARED)
            .type();

        when(ctxMocks.getContext().isEnumType(type)).thenReturn(isEnum);
        when(ctxMocks.getContext().isMaplike(type)).thenReturn(isMaplike);
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContextOuter);
        assertThat(typeInfo.isEnumType()).isEqualTo(isEnum);
        assertThat(typeInfo.isMapType()).isEqualTo(isMaplike);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContextOuter);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContextOuter);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Container");
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).map(t -> (TypeVariableInfo) t).satisfiesExactly(
              typeArg -> {
                  softly.assertThat(typeArg.name()).isEqualTo("T");
                  softly.assertThat(typeArg.qualifiedName()).isEqualTo("com.github.cuzfrog.Outer@T");
                  softly.assertThat(typeArg.contextTypeQualifiedName()).isEqualTo("com.github.cuzfrog.Outer");
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
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContextOuter);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            softly.assertThat(typeInfo.resolved()).isTrue();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseOptionalType() {
        when(ctxMocks.getContext().isOptionalType("java.util.Optional")).thenReturn(true);
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("java.util.Optional").type())
            .withTypeKind(TypeKind.DECLARED)
            .withTypeArguments(ctxMocks.typeElement("java.lang.String").type())
            .type();
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContextOuter);
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.Optional");
        assertThat(typeInfo.typeArgs()).hasSize(1);
        assertThat(typeInfo.resolved()).isTrue();
    }

    @Test
    void reuseDeclaredTypeInfoFromCache() {
        var type = ctxMocks.typeElement("com.github.cuzfrog.Abc").type();
        var cachedTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.Abc")
            .resolved(false)
            .build();
        ctxMocks.getTypeStore().saveTypeInfo("com.github.cuzfrog.Abc", Collections.emptyList(), cachedTypeInfo);
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, typeContext);
        assertThat(typeInfo).isSameAs(cachedTypeInfo);
    }

    @Test @Issue(44)
    void shouldCacheTypeInfoWithTypeArgsIfIsGeneric() {
        var type = ctxMocks.typeElement("com.github.cuzfrog.Container")
            .withTypeArguments(ctxMocks.typeElement("java.lang.Integer").type()).type();
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, TypeContext.builder().typeDef(ClassDef.builder().qualifiedName("com.github.cuzfrog.Container").build()).build());
        assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Container");
        assertThat(typeInfo.typeArgs()).hasSize(1);
        var typeArg = (ConcreteTypeInfo)typeInfo.typeArgs().getFirst();
        assertThat(typeArg.qualifiedName()).isEqualTo("java.lang.Integer");

        verify(ctxMocks.getTypeStore()).getTypeInfo("com.github.cuzfrog.Container", Collections.singletonList(typeArg));
        verify(ctxMocks.getTypeStore()).saveTypeInfo(eq("com.github.cuzfrog.Container"), eq(Collections.singletonList(typeArg)), any());
        verify(ctxMocks.getTypeStore()).getTypeInfo("java.lang.Integer", Collections.emptyList());
    }
}

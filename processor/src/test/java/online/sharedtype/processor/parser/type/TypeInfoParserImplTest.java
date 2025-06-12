package online.sharedtype.processor.parser.type;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.Issue;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TypeInfoParserImplTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParserImpl parser = new TypeInfoParserImpl(ctxMocks.getContext());

    private final TypeElement ctxTypeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
    private final TypeElement ctxTypeElementOuter = ctxMocks.typeElement("com.github.cuzfrog.Outer").element();

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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElement);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(expectedName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(expectedName);
            softly.assertThat(typeInfo.resolved()).isTrue();
        });

        var arrayType = ctxMocks.array(type).type();
        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(arrayType, ctxTypeElement);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElement);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo(qualifiedName);
            softly.assertThat(typeInfo.simpleName()).isEqualTo(qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1));
            softly.assertThat(typeInfo.resolved()).isTrue();
        });

        var arrayType = ctxMocks.array(type).type();
        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(arrayType, ctxTypeElement);
        SoftAssertions.assertSoftly(softly -> {
            var componentTypeInfo = (ConcreteTypeInfo) arrayTypeInfo.component();
            softly.assertThat(componentTypeInfo.resolved()).isTrue();
        });
    }

    @Test
    void parseArraylikeObject() {
        DeclaredType arraylikeTopType = ctxMocks.typeElement("java.util.Iterable")
            .withTypeArguments(ctxMocks.typeElement("java.lang.String").type()).type();
        when(ctxMocks.getContext().isArraylike(arraylikeTopType)).thenReturn(true);
        when(ctxMocks.getContext().isTopArrayType(arraylikeTopType)).thenReturn(true);

        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("java.util.List").type())
            .withTypeKind(TypeKind.DECLARED)
            .withTypeArguments(ctxMocks.typeElement("java.lang.String").type())
            .withSuperTypes(arraylikeTopType)
            .type();
        when(ctxMocks.getContext().isArraylike(type)).thenReturn(true);

        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(type, ctxTypeElement);
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
        DeclaredType arraylikeTopTypeOfString = ctxMocks.typeElement("java.util.Iterable")
            .withTypeArguments(ctxMocks.typeElement("java.lang.String").type()).type();
        when(ctxMocks.getContext().isArraylike(arraylikeTopTypeOfString)).thenReturn(true);
        when(ctxMocks.getContext().isTopArrayType(arraylikeTopTypeOfString)).thenReturn(true);

        var nestedType = ctxMocks.typeElement("java.lang.Set")
            .withTypeArguments(
                ctxMocks.typeElement("java.lang.String").type()
            )
            .withSuperTypes(arraylikeTopTypeOfString)
            .type();

        DeclaredType arraylikeTopTypeOfSet = ctxMocks.typeElement("java.util.Iterable")
            .withTypeArguments(nestedType).type();
        when(ctxMocks.getContext().isArraylike(arraylikeTopTypeOfSet)).thenReturn(true);
        when(ctxMocks.getContext().isTopArrayType(arraylikeTopTypeOfSet)).thenReturn(true);

        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("java.util.List").type())
            .withTypeKind(TypeKind.DECLARED)
            .withTypeArguments(nestedType)
            .withSuperTypes(arraylikeTopTypeOfSet)
            .type();
        when(ctxMocks.getContext().isArraylike(type)).thenReturn(true);
        when(ctxMocks.getContext().isArraylike(nestedType)).thenReturn(true);

        var arrayTypeInfo = (ArrayTypeInfo) parser.parse(type, ctxTypeElement);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElementOuter);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
            softly.assertThat(typeInfo.simpleName()).isEqualTo("Abc");
            softly.assertThat(typeInfo.resolved()).isFalse();
            softly.assertThat(typeInfo.typeArgs()).isEmpty();
        });
    }

    @Test
    void parseDateTimeLike() {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("java.time.LocalDate").type())
            .withTypeKind(TypeKind.DECLARED)
            .type();
        when(ctxMocks.getContext().isDatetimelike(type)).thenReturn(true);

        var typeInfo = (DateTimeInfo) parser.parse(type, ctxTypeElementOuter);
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.time.LocalDate");
    }

    @ParameterizedTest
    @CsvSource({
        "false, false, OTHER",
        "true, false, ENUM",
        "false, true, MAP",
    })
    void setTypeKind(boolean isEnum, boolean isMaplike, ConcreteTypeInfo.Kind expectedKind) {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("com.github.cuzfrog.SomeType").type())
            .withTypeKind(TypeKind.DECLARED)
            .type();

        when(ctxMocks.getContext().isEnumType(type)).thenReturn(isEnum);
        when(ctxMocks.getContext().isMaplike(type)).thenReturn(isMaplike);
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElementOuter);
        assertThat(typeInfo.getKind()).isEqualTo(expectedKind);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElementOuter);
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

        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElementOuter);
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
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElementOuter);
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
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElementOuter);
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.Optional");
        assertThat(typeInfo.typeArgs()).hasSize(1);
        assertThat(typeInfo.resolved()).isTrue();
    }

    @Test
    void parseMapType() {
        throw new AssertionError("TODO");
    }

    @Test
    void reportKindError() {
        var type = ctxMocks.declaredTypeVariable("field1", ctxMocks.typeElement("a.b.A").type())
            .withTypeKind(TypeKind.ERROR)
            .type();
        var res = parser.parse(type, ctxTypeElementOuter);
        assertThat(res).isEqualTo(TypeInfo.NO_TYPE_INFO);
        verify(ctxMocks.getContext()).error(eq(ctxTypeElementOuter), argThat(s -> s.contains("type is not visible in the scope")), any(Object[].class));
    }

    @Test
    void reuseDeclaredTypeInfoFromCache() {
        var type = ctxMocks.typeElement("com.github.cuzfrog.Abc").type();
        var cachedTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.Abc")
            .resolved(false)
            .build();
        ctxMocks.getTypeStore().saveTypeInfo("com.github.cuzfrog.Abc", Collections.emptyList(), cachedTypeInfo);
        var typeInfo = (ConcreteTypeInfo) parser.parse(type, ctxTypeElement);
        assertThat(typeInfo).isSameAs(cachedTypeInfo);
    }

    @Test
    @Issue(44)
    void shouldCacheTypeInfoWithTypeArgsIfIsGeneric() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Container")
            .withTypeArguments(ctxMocks.typeElement("java.lang.Integer").type()).element();
        var typeInfo = (ConcreteTypeInfo) parser.parse(typeElement.asType(), typeElement);
        assertThat(typeInfo.qualifiedName()).isEqualTo("com.github.cuzfrog.Container");
        assertThat(typeInfo.typeArgs()).hasSize(1);
        var typeArg = (ConcreteTypeInfo) typeInfo.typeArgs().getFirst();
        assertThat(typeArg.qualifiedName()).isEqualTo("java.lang.Integer");

        verify(ctxMocks.getTypeStore()).getTypeInfo("com.github.cuzfrog.Container", Collections.singletonList(typeArg));
        verify(ctxMocks.getTypeStore()).saveTypeInfo(eq("com.github.cuzfrog.Container"), eq(Collections.singletonList(typeArg)), any());
        verify(ctxMocks.getTypeStore()).getTypeInfo("java.lang.Integer", Collections.emptyList());
    }
}

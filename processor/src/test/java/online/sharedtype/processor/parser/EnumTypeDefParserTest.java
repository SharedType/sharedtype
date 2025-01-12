package online.sharedtype.processor.parser;

import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TypeElementMock;
import online.sharedtype.processor.parser.type.TypeContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class EnumTypeDefParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final EnumTypeDefParser parser = new EnumTypeDefParser(ctxMocks.getContext(), typeInfoParser);

    private final TypeElementMock enumType = ctxMocks.typeElement("com.github.cuzfrog.EnumA")
        .withElementKind(ElementKind.ENUM);

    private final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

    @Test
    void simpleEnum() {
        enumType.withEnclosedElements(
            ctxMocks.declaredTypeVariable("Value1", enumType.type()).withElementKind(ElementKind.ENUM_CONSTANT).element(),
            ctxMocks.declaredTypeVariable("Value2", enumType.type()).withElementKind(ElementKind.ENUM_CONSTANT).element()
        );

        EnumDef typeDef = (EnumDef)parser.parse(enumType.element());
        assert typeDef != null;
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.EnumA");
        assertThat(typeDef.simpleName()).isEqualTo("EnumA");
        assertThat(typeDef.components()).satisfiesExactly(
            c1 -> {
                assertThat(c1.value()).isEqualTo("Value1");
                assertThat(c1.name()).isEqualTo("Value1");
            },
            c2 -> {
                assertThat(c2.value()).isEqualTo("Value2");
                assertThat(c2.name()).isEqualTo("Value2");
            }
        );
    }

    @Test
    void enumValueMarkedOnField() {
        var field2 = ctxMocks.primitiveVariable("field2", TypeKind.CHAR).withAnnotation(SharedType.EnumValue.class);
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR)
                .withParameters(
                    ctxMocks.primitiveVariable("field1", TypeKind.INT).element(),
                    ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                        )
                    )
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value2", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(200), ctxMocks.literalTree('b')
                        )
                    )
                )
                .element(),
            ctxMocks.primitiveVariable("field1", TypeKind.INT).element(),
            field2.element()
        );
        when(typeInfoParser.parse(field2.type(), TypeContext.builder().qualifiedName("com.github.cuzfrog.EnumA").build()))
            .thenReturn(Constants.CHAR_TYPE_INFO);

        EnumDef typeDef = (EnumDef)parser.parse(enumType.element());
        assert typeDef != null;
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.EnumA");
        assertThat(typeDef.simpleName()).isEqualTo("EnumA");
        assertThat(typeDef.components()).satisfiesExactly(
            c1 -> {
                assertThat(c1.value()).isEqualTo('a');
                assertThat(c1.name()).isEqualTo("Value1");
                assertThat(c1.type()).isEqualTo(Constants.CHAR_TYPE_INFO);
            },
            c2 -> {
                assertThat(c2.value()).isEqualTo('b');
                assertThat(c2.name()).isEqualTo("Value2");
                assertThat(c2.type()).isEqualTo(Constants.CHAR_TYPE_INFO);
            }
        );
    }

    @Test
    void enumValueMarkedOnConstructorParameter() {
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR)
                .withParameters(
                    ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element(),
                    ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                        )
                    )
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value2", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(200), ctxMocks.literalTree('b')
                        )
                    )
                )
                .element(),
            ctxMocks.primitiveVariable("field1", TypeKind.INT).element(),
            ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
        );

        var typeDef = (EnumDef)parser.parse(enumType.element());
        assert typeDef != null;
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.EnumA");
        assertThat(typeDef.simpleName()).isEqualTo("EnumA");
        assertThat(typeDef.components()).satisfiesExactly(
            c1 -> assertThat(c1.value()).isEqualTo(100),
            c2 -> assertThat(c2.value()).isEqualTo(200)
        );
    }

    @Test
    void failWhenMoreThanOneEnumValueMarkedOnField() {
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR)
                .withParameters(
                    ctxMocks.primitiveVariable("field1", TypeKind.INT).element(),
                    ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                        )
                    )
                )
                .element(),
            ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element(),
            ctxMocks.primitiveVariable("field2", TypeKind.CHAR).withAnnotation(SharedType.EnumValue.class).element()
        );

        parser.parse(enumType.element());
        verify(ctxMocks.getContext()).error(msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("multiple annotation");
    }

    @Test
    void failWhenMoreThanOneEnumValueMarkedOnConstructorParameter() {
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR)
                .withParameters(
                    ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element(),
                    ctxMocks.primitiveVariable("field2", TypeKind.CHAR).withAnnotation(SharedType.EnumValue.class).element()
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                        )
                    )
                )
                .element(),
            ctxMocks.primitiveVariable("field1", TypeKind.INT).element(),
            ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
        );

        parser.parse(enumType.element());
        verify(ctxMocks.getContext()).error(msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("multiple annotation");
    }

    @Test
    void failWhenMoreThanOneEnumValueMarkedOnConstructorParameterAndField() {
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR)
                .withParameters(
                    ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element(),
                    ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                        )
                    )
                )
                .element(),
            ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element(),
            ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
        );

        parser.parse(enumType.element());
        verify(ctxMocks.getContext()).error(msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("multiple annotation");
    }

    @Test
    void failWhenConstructorHasNoParameter() {
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR).element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                        )
                    )
                )
                .element(),
            ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element(),
            ctxMocks.primitiveVariable("field2", TypeKind.CHAR).element()
        );
        parser.parse(enumType.element());
        verify(ctxMocks.getContext()).error(msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("Lombok");
    }

    @Test
    void failWhenEnumValueTypeIsNotLiteral() {
        var enumB = ctxMocks.typeElement("com.github.cuzfrog.EnumB").withElementKind(ElementKind.ENUM);
        enumType.withEnclosedElements(
            ctxMocks.executable("EnumA").withElementKind(ElementKind.CONSTRUCTOR)
                .withParameters(
                    ctxMocks.declaredTypeVariable("field1", enumB.type()).withAnnotation(SharedType.EnumValue.class).element()
                )
                .element(),
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.identifierTree("FOO")
                        )
                    )
                )
                .element(),
            ctxMocks.declaredTypeVariable("field1", enumB.type()).element()
        );

        parser.parse(enumType.element());
        verify(ctxMocks.getContext()).error(msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("Only literals are supported");
    }
}

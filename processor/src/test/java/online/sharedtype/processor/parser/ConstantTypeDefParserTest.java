package online.sharedtype.processor.parser;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.DependingKind;
import online.sharedtype.processor.parser.type.TypeContext;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConstantTypeDefParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final ConstantTypeDefParser parser = new ConstantTypeDefParser(ctxMocks.getContext(), typeInfoParser);

    private final TypeContext typeContext = TypeContext.builder()
        .typeDef(ConstantNamespaceDef.builder().qualifiedName("com.github.cuzfrog.Abc").build())
        .dependingKind(DependingKind.COMPONENTS).build();
    private final ClassDef mainTypeDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Abc").build();
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        ctxMocks.getTypeStore().saveTypeDef("com.github.cuzfrog.Abc", mainTypeDef);
        ctxMocks.getTypeStore().saveConfig(mainTypeDef.qualifiedName(), config);
    }

    @Test
    void skipMapType() {
        mainTypeDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("java.util.Map").mapType(true).build());
        assertThat(parser.parse(ctxMocks.typeElement("com.github.cuzfrog.Abc").element())).isEmpty();
    }

    @Test
    void skipArrayType() {
        mainTypeDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("java.util.List").arrayType(true).build());
        assertThat(parser.parse(ctxMocks.typeElement("com.github.cuzfrog.Abc").element())).isEmpty();
    }

    @Test
    void skipClassWithoutStaticField() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
        assertThat(parser.parse(typeElement)).isEmpty();
    }

    @Test
    void skipIfConfigNotIncludeConstants() {
        when(config.includes(SharedType.ComponentType.CONSTANTS)).thenReturn(false);
        assertThat(parser.parse(ctxMocks.typeElement("com.github.cuzfrog.Abc").element())).isEmpty();
    }

    @Test
    void parse() {
        when(config.includes(SharedType.ComponentType.CONSTANTS)).thenReturn(true);
        var ignoredField = ctxMocks.primitiveVariable("SHOULD_BE_IGNORED", TypeKind.INT)
            .withModifiers(Modifier.STATIC);
        when(ctxMocks.getContext().isIgnored(ignoredField.element())).thenReturn(true);
        var intStaticField = ctxMocks.primitiveVariable("CONST_INT_VALUE", TypeKind.INT)
            .withModifiers(Modifier.STATIC)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.literalTree(105).getTree()
                )
            );
        var stringStaticField = ctxMocks.declaredTypeVariable("CONST_STRING_VALUE", ctxMocks.typeElement("java.lang.String").type())
            .withModifiers(Modifier.STATIC).withElementKind(ElementKind.FIELD)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.literalTree("abc123").getTree()
                )
            );
        var nonStaticField = ctxMocks.primitiveVariable("nonStaticField", TypeKind.LONG)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.literalTree(888).getTree()
                )
            );
        TypeElement typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withEnclosedElements(
                ignoredField.element(),
                intStaticField.element(),
                stringStaticField.element(),
                nonStaticField.element()
            )
            .element();
        when(typeInfoParser.parse(intStaticField.type(), typeContext)).thenReturn(Constants.INT_TYPE_INFO);
        when(typeInfoParser.parse(stringStaticField.type(), typeContext)).thenReturn(Constants.STRING_TYPE_INFO);

        var typeDef = (ConstantNamespaceDef)parser.parse(typeElement).get(0);
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
        assertThat(typeDef.components()).satisfiesExactly(
            field1 -> {
                assertThat(field1.name()).isEqualTo("CONST_INT_VALUE");
                assertThat(field1.type()).isEqualTo(Constants.INT_TYPE_INFO);
                assertThat(field1.value()).isEqualTo(105);
            },
            field2 -> {
                assertThat(field2.name()).isEqualTo("CONST_STRING_VALUE");
                assertThat(field2.type()).isEqualTo(Constants.STRING_TYPE_INFO);
                assertThat(field2.value()).isEqualTo("abc123");
            }
        );
    }
}

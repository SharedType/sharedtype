package online.sharedtype.processor.parser;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.parser.value.ValueParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ConstantTypeDefParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final ValueParser valueParser = mock(ValueParser.class);
    private final ConstantTypeDefParser parser = new ConstantTypeDefParser(ctxMocks.getContext(), typeInfoParser, valueParser);

    private final ClassDef mainTypeDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Abc").annotated(true).build();
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        ctxMocks.getTypeStore().saveTypeDef("com.github.cuzfrog.Abc", mainTypeDef);
        when(config.getQualifiedName()).thenReturn("com.github.cuzfrog.Abc");
        ctxMocks.getTypeStore().saveConfig(config);
    }

    @Test
    void skipMapType() {
        mainTypeDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("java.util.Map").kind(ConcreteTypeInfo.Kind.MAP).build());
        assertThat(parser.parse(ctxMocks.typeElement("com.github.cuzfrog.Abc").element())).isEmpty();
    }

    @Test
    void skipWithoutExplicitAnnotation() {
        ClassDef mainTypeDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.AnotherReferenced").annotated(false).build();
        ctxMocks.getTypeStore().saveTypeDef("com.github.cuzfrog.AnotherReferenced", mainTypeDef);
        assertThat(parser.parse(ctxMocks.typeElement("com.github.cuzfrog.AnotherReferenced").element())).isEmpty();
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
    void parseSimpleLiteralValues() {
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
        when(typeInfoParser.parse(intStaticField.type(), typeElement)).thenReturn(Constants.INT_TYPE_INFO);
        when(typeInfoParser.parse(stringStaticField.type(), typeElement)).thenReturn(Constants.STRING_TYPE_INFO);
        when(valueParser.resolve(intStaticField.element(), typeElement)).thenReturn(ValueHolder.of(Constants.INT_TYPE_INFO, 105));
        when(valueParser.resolve(stringStaticField.element(), typeElement)).thenReturn(ValueHolder.of(Constants.STRING_TYPE_INFO, "abc123"));

        var typeDef = (ConstantNamespaceDef)parser.parse(typeElement).get(0);
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.Abc");
        assertThat(typeDef.components()).satisfiesExactly(
            field1 -> {
                assertThat(field1.name()).isEqualTo("CONST_INT_VALUE");
                assertThat(field1.value().getValueType()).isEqualTo(Constants.INT_TYPE_INFO);
                assertThat(field1.value().getValue()).isEqualTo(105);
                assertThat(field1.value().literalValue()).isEqualTo("105");
                assertThat(field1.getElement()).isEqualTo(intStaticField.element());
            },
            field2 -> {
                assertThat(field2.name()).isEqualTo("CONST_STRING_VALUE");
                assertThat(field2.value().getValueType()).isEqualTo(Constants.STRING_TYPE_INFO);
                assertThat(field2.value().getValue()).isEqualTo("abc123");
                assertThat(field2.value().literalValue()).isEqualTo("\"abc123\"");
                assertThat(field2.getElement()).isEqualTo(stringStaticField.element());
            }
        );
    }
}

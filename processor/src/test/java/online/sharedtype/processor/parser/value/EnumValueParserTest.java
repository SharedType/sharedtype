package online.sharedtype.processor.parser.value;

import com.sun.source.tree.LiteralTree;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.EnumCtorIndex;
import online.sharedtype.processor.context.TypeElementMock;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.support.exception.SharedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class EnumValueParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final ValueResolverBackend valueResolverBackend = mock(ValueResolverBackend.class);
    private final EnumValueParser resolver = new EnumValueParser(ctxMocks.getContext(), typeInfoParser, valueResolverBackend);

    private final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    private final TypeElementMock enumType = ctxMocks.typeElement("com.github.cuzfrog.EnumA");

    @BeforeEach
    void setup() {
        when(valueResolverBackend.recursivelyResolve(any())).then(invoc -> {
            var tree = invoc.getArgument(0, ValueResolveContext.class).getTree();
            if (tree instanceof LiteralTree) {
                return ((LiteralTree) tree).getValue();
            }
            throw new AssertionError("Failed test. Unexpected tree: " + tree);
        });
    }

    @Test
    void simpleEnum() {
        var enumConstant1 = ctxMocks.declaredTypeVariable("VALUE1", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .element();
        enumType.withEnclosedElements(enumConstant1);
        var enumTypeInfo = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").kind(ConcreteTypeInfo.Kind.ENUM).build();
        when(typeInfoParser.parse(enumType.type(), enumType.element())).thenReturn(enumTypeInfo);

        EnumConstantValue value1 = (EnumConstantValue) resolver.resolve(enumConstant1, enumType.element());
        assertThat(value1.getValue()).isEqualTo("VALUE1");
        assertThat(value1.getEnumConstantName()).isEqualTo("VALUE1");
        assertThat(value1.getValueType()).isEqualTo(enumTypeInfo);
    }

    @Test
    void enumReferencingEnum() {
        var anotherEnumTypeInfo = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.AnotherEnum").kind(ConcreteTypeInfo.Kind.ENUM).build();
        var enumConstant1 = ctxMocks.declaredTypeVariable("Value1", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.newClassTree().withArguments(
                        ctxMocks.identifierTree("AnotherEnumConstant1").getTree()
                    ).getTree()
                )
            )
            .element();
        when(ctxMocks.getContext().getTypeStore().getEnumValueIndex("com.github.cuzfrog.EnumA"))
            .thenReturn(new EnumCtorIndex(0, enumConstant1));
        reset(valueResolverBackend);
        when(valueResolverBackend.recursivelyResolve(any()))
            .thenReturn(ValueHolder.ofEnum("AnotherEnumConstant1", anotherEnumTypeInfo, "AnotherEnumConstant1"));

        EnumConstantValue value1 = (EnumConstantValue) resolver.resolve(enumConstant1, enumType.element());
        assertThat(value1.getEnumConstantName()).isEqualTo(enumConstant1.getSimpleName().toString());
        assertThat(value1.getValue()).isEqualTo("AnotherEnumConstant1");
        assertThat(value1.getValueType()).isEqualTo(anotherEnumTypeInfo);
    }

    @Test
    void enumValueMarkedOnField() {
        var field1 = ctxMocks.primitiveVariable("field1", TypeKind.INT);
        var field2 = ctxMocks.primitiveVariable("field2", TypeKind.CHAR).withAnnotation(SharedType.EnumValue.class);
        var valueTree1 = ctxMocks.literalTree('a');
        var valueTree2 = ctxMocks.literalTree('b');
        var enumConstant1 = ctxMocks.declaredTypeVariable("Value1", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.newClassTree().withArguments(
                        ctxMocks.literalTree(100).getTree(), valueTree1.getTree()
                    ).getTree()
                )
            )
            .element();
        var enumConstant2 = ctxMocks.declaredTypeVariable("Value2", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.newClassTree().withArguments(
                        ctxMocks.literalTree(200).getTree(), valueTree2.getTree()
                    ).getTree()
                )
            )
            .element();
        enumType.withEnclosedElements(
            enumConstant1,
            enumConstant2,
            field1.element(),
            field2.element()
        );
        when(ctxMocks.getContext().isAnnotatedAsEnumValue(field2.element())).thenReturn(true);
        when(typeInfoParser.parse(field2.type(), enumType.element())).thenReturn(Constants.CHAR_TYPE_INFO);

        EnumConstantValue value1 = (EnumConstantValue) resolver.resolve(enumConstant1, enumType.element());
        assertThat(value1.getEnumConstantName()).isEqualTo(enumConstant1.getSimpleName().toString());
        assertThat(value1.getValue()).isEqualTo('a');
        assertThat(value1.getValueType()).isEqualTo(Constants.CHAR_TYPE_INFO);
        var value2 = resolver.resolve(enumConstant2, enumType.element());
        assertThat(value2.getValue()).isEqualTo('b');
    }

    @Test
    void failWhenMoreThanOneEnumValueMarkedOnField() {
        var field1 = ctxMocks.primitiveVariable("field1", TypeKind.INT).withAnnotation(SharedType.EnumValue.class).element();
        var field2 = ctxMocks.primitiveVariable("field2", TypeKind.CHAR).withAnnotation(SharedType.EnumValue.class).element();
        enumType.withEnclosedElements(
            ctxMocks.declaredTypeVariable("Value1", enumType.type())
                .withElementKind(ElementKind.ENUM_CONSTANT)
                .ofTree(
                    ctxMocks.variableTree().withInitializer(
                        ctxMocks.newClassTree().withArguments(
                            ctxMocks.literalTree(100).getTree(), ctxMocks.literalTree('a').getTree()
                        ).getTree()
                    )
                )
                .element(),
            field1,
            field2
        );
        when(ctxMocks.getContext().isAnnotatedAsEnumValue(any())).thenReturn(true);

        resolver.resolveCtorIndex(enumType.element());
        verify(ctxMocks.getContext()).error(any(), msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("only one field can be annotated as enum value");
    }

    @Test
    void logErrorWhenBackendThrowsException() {
        var classB = ctxMocks.typeElement("com.github.cuzfrog.EnumB").withElementKind(ElementKind.CLASS);
        var field1 = ctxMocks.declaredTypeVariable("field1", classB.type())
            .withElementKind(ElementKind.FIELD)
            .withAnnotation(SharedType.EnumValue.class)
            .element();
        var enumConstant1 = ctxMocks.declaredTypeVariable("Value1", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.newClassTree().withArguments(
                        ctxMocks.identifierTree("FOO").getTree()
                    ).getTree()
                )
            )
            .element();
        enumType.withEnclosedElements(enumConstant1, field1);
        when(ctxMocks.getContext().isAnnotatedAsEnumValue(field1)).thenReturn(true);

        reset(valueResolverBackend);
        when(valueResolverBackend.recursivelyResolve(any())).thenThrow(new SharedTypeException("test msg"));

        resolver.resolve(enumConstant1, enumType.element());
        verify(ctxMocks.getContext()).error(eq(enumType.element()), msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("Failed to parse argument value");
    }
}

package online.sharedtype.processor.parser.value;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TypeElementMock;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class EnumValueResolverTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final EnumValueResolver resolver = new EnumValueResolver(ctxMocks.getContext(), typeInfoParser);

    private final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    private final TypeElementMock enumType = ctxMocks.typeElement("com.github.cuzfrog.EnumA");

    @Test
    void enumValueMarkedOnField() {
        var field1 = ctxMocks.primitiveVariable("field1", TypeKind.INT);
        var field2 = ctxMocks.primitiveVariable("field2", TypeKind.CHAR).withAnnotation(SharedType.EnumValue.class);
        var enumConstant1 = ctxMocks.declaredTypeVariable("Value1", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.newClassTree().withArguments(
                        ctxMocks.literalTree(100), ctxMocks.literalTree('a')
                    ).getTree()
                )
            )
            .element();
        var enumConstant2 = ctxMocks.declaredTypeVariable("Value2", enumType.type())
            .withElementKind(ElementKind.ENUM_CONSTANT)
            .ofTree(
                ctxMocks.variableTree().withInitializer(
                    ctxMocks.newClassTree().withArguments(
                        ctxMocks.literalTree(200), ctxMocks.literalTree('b')
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

        EnumConstantValue value1 = (EnumConstantValue) resolver.resolve(enumConstant1, enumType.element());
        assertThat(value1.getEnumConstantName()).isEqualTo(enumConstant1.getSimpleName().toString());
        assertThat(value1.getValue()).isEqualTo('a');
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
                            ctxMocks.literalTree(100), ctxMocks.literalTree('a')
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
    void failWhenEnumValueTypeIsNotLiteral() {
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
                        ctxMocks.identifierTree("FOO")
                    ).getTree()
                )
            )
            .element();
        enumType.withEnclosedElements(enumConstant1, field1);
        when(ctxMocks.getContext().isAnnotatedAsEnumValue(field1)).thenReturn(true);

        resolver.resolve(enumConstant1, enumType.element());
        verify(ctxMocks.getContext()).error(eq(enumType.element()), msgCaptor.capture(), any(Object[].class));
        assertThat(msgCaptor.getValue()).contains("Only literals are supported");
    }
}

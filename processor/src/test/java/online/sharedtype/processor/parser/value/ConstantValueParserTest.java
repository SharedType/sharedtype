package online.sharedtype.processor.parser.value;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ConstantValueParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final ValueResolverBackend valueResolverBackend = mock(ValueResolverBackend.class);
    private final ConstantValueParser resolver = new ConstantValueParser(ctxMocks.getContext(), typeInfoParser, valueResolverBackend);

    private final ArgumentCaptor<ValueResolveContext> valueResolveContextCaptor = ArgumentCaptor.forClass(ValueResolveContext.class);

    @Test
    void resolve() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
        var fieldTree = ctxMocks.variableTree().withInitializer(ctxMocks.literalTree(123).getTree());
        var fieldElement = ctxMocks.primitiveVariable("field1", TypeKind.INT)
            .withEnclosingElement(typeElement)
            .ofTree(fieldTree)
            .element();

        when(typeInfoParser.parse(fieldElement.asType(), typeElement)).thenReturn(Constants.INT_TYPE_INFO);
        when(valueResolverBackend.recursivelyResolve(any())).thenReturn(123);

        var value = resolver.resolve(fieldElement, typeElement);
        assertThat(value.getValue()).isEqualTo(123);
        assertThat(value.getValueType()).isEqualTo(Constants.INT_TYPE_INFO);

        verify(valueResolverBackend).recursivelyResolve(valueResolveContextCaptor.capture());
        var valueResolveContext = valueResolveContextCaptor.getValue();
        assertThat(valueResolveContext.getEnclosingTypeElement()).isEqualTo(typeElement);
        assertThat(valueResolveContext.getFieldElement()).isEqualTo(fieldElement);
        assertThat(valueResolveContext.getTree()).isEqualTo(fieldTree.getTree());
    }
}

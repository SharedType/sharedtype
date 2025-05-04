package online.sharedtype.processor.parser.value;

import com.sun.source.tree.Scope;
import online.sharedtype.processor.context.ContextMocks;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ValueResolveUtilsTest {
    private final ContextMocks ctxMocks = new ContextMocks();

    @Test
    void getEnclosingTypeElement() {
        var outerElement = ctxMocks.typeElement("com.github.cuzfrog.Outer").element();
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
            .withEnclosingElement(outerElement).element();
        assertThat(ValueResolveUtils.getEnclosingTypeElement(typeElement)).isSameAs(outerElement);
    }

    @Test
    void getValueTree() {
        var literalTree = ctxMocks.literalTree("1").getTree();
        assertThat(ValueResolveUtils.getValueTree(literalTree, null)).isSameAs(literalTree);

        var variableTree = ctxMocks.variableTree().withInitializer(literalTree).getTree();
        assertThat(ValueResolveUtils.getValueTree(variableTree, null)).isSameAs(literalTree);

        var assignmentTree = ctxMocks.assignmentTree().withExpression(literalTree).getTree();
        assertThat(ValueResolveUtils.getValueTree(assignmentTree, null)).isSameAs(literalTree);

        var identifierTree = ctxMocks.identifierTree("a").getTree();
        assertThat(ValueResolveUtils.getValueTree(identifierTree, null)).isSameAs(identifierTree);

        var memberSelectTree = ctxMocks.memberSelectTree("a.b").getTree();
        assertThat(ValueResolveUtils.getValueTree(memberSelectTree, null)).isSameAs(memberSelectTree);
    }

    @Test
    void findElementInLocalScopeFromTreeScope() {
        var fieldElement = ctxMocks.primitiveVariable("field1", TypeKind.INT).withElementKind(ElementKind.FIELD).element();
        Scope scope = mock(Scope.class);
        Scope enclosingScope = mock(Scope.class);
        when(scope.getEnclosingScope()).thenReturn(enclosingScope);
        when(enclosingScope.getLocalElements()).thenAnswer(invoc -> List.of(fieldElement));

        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
        assertThat(ValueResolveUtils.findElementInLocalScope(scope, "field1", typeElement)).isSameAs(fieldElement);
    }

    @Test
    void findElementInLocalScopeFromEnclosedElements() {
        var typeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc").element();
        var fieldElement = ctxMocks.primitiveVariable("field1", TypeKind.INT)
            .withElementKind(ElementKind.FIELD)
            .withEnclosingElement(typeElement).element();
        assertThat(ValueResolveUtils.findElementInLocalScope(null, "field1", typeElement)).isSameAs(fieldElement);
    }
}

package online.sharedtype.processor.parser.value;

import online.sharedtype.processor.context.ContextMocks;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}

package online.sharedtype.processor.context;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MemberSelectTreeMock extends ExpressionTreeMock<MemberSelectTree, MemberSelectTreeMock> {
    MemberSelectTreeMock(String identifier, Context ctx) {
        super(mock(MemberSelectTree.class), ctx);
        when(tree.getIdentifier()).thenReturn(new MockName(identifier));
        when(tree.toString()).thenReturn(identifier);
        when(tree.getKind()).thenReturn(Tree.Kind.MEMBER_SELECT);
    }

    public MemberSelectTreeMock withExpression(ExpressionTree expressionTree) {
        when(tree.getExpression()).thenReturn(expressionTree);
        return this;
    }
}

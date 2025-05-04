package online.sharedtype.processor.context;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;

import javax.lang.model.element.Name;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MemberSelectTreeMock extends AbstractTreeMock<MemberSelectTree, MemberSelectTreeMock> {
    MemberSelectTreeMock(String identifier, Context ctx) {
        super(mock(MemberSelectTree.class), ctx);
        when(tree.getIdentifier()).thenReturn(new MockName(identifier));
    }

    public MemberSelectTreeMock withExpression(ExpressionTree expressionTree) {
        when(tree.getExpression()).thenReturn(expressionTree);
        return this;
    }
}

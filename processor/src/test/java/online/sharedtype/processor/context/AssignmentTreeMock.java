package online.sharedtype.processor.context;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AssignmentTreeMock extends AbstractTreeMock<AssignmentTree, AssignmentTreeMock> {
    AssignmentTreeMock(Context ctx) {
        super(mock(AssignmentTree.class), ctx);
    }

    public AssignmentTreeMock withVariable(ExpressionTree variableTree) {
        when(tree.getVariable()).thenReturn(variableTree);
        return this;
    }

    public AssignmentTreeMock withExpression(ExpressionTree expressionTree) {
        when(tree.getExpression()).thenReturn(expressionTree);
        return this;
    }
}

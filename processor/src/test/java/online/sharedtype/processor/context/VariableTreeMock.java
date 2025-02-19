package online.sharedtype.processor.context;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class VariableTreeMock extends AbstractTreeMock<VariableTree, VariableTreeMock> {
    VariableTreeMock(Context ctx) {
        super(mock(VariableTree.class), ctx);
    }

    public VariableTreeMock withInitializer(ExpressionTree initializer) {
        when(tree.getInitializer()).thenReturn(initializer);
        return this;
    }
}

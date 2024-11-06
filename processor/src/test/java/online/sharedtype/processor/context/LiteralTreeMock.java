package online.sharedtype.processor.context;

import com.sun.source.tree.LiteralTree;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class LiteralTreeMock extends ExpressionTreeMock<LiteralTree, LiteralTreeMock> {
    LiteralTreeMock(Object value, Context ctx) {
        super(mock(LiteralTree.class), ctx);
        when(tree.getValue()).thenReturn(value);
    }
}

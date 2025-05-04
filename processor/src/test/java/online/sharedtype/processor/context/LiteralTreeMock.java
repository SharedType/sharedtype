package online.sharedtype.processor.context;

import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class LiteralTreeMock extends ExpressionTreeMock<LiteralTree, LiteralTreeMock> {
    LiteralTreeMock(Object value, Context ctx) {
        super(mock(LiteralTree.class), ctx);
        when(tree.getValue()).thenReturn(value);
        when(tree.getKind()).thenReturn(getKind(value));
    }

    private static Tree.Kind getKind(Object value) {
        return switch (value) {
            case String ignored -> Tree.Kind.STRING_LITERAL;
            case Integer ignored -> Tree.Kind.INT_LITERAL;
            case Long ignored -> Tree.Kind.LONG_LITERAL;
            case Float ignored -> Tree.Kind.FLOAT_LITERAL;
            case Double ignored -> Tree.Kind.DOUBLE_LITERAL;
            case Character ignored -> Tree.Kind.CHAR_LITERAL;
            case Boolean ignored -> Tree.Kind.BOOLEAN_LITERAL;
            case null -> Tree.Kind.NULL_LITERAL;
            default -> throw new IllegalArgumentException("Unsupported literal type: " + value.getClass());
        };
    }
}

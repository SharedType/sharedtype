package org.sharedtype.processor.context;

import com.sun.source.tree.ExpressionTree;

public abstract class ExpressionTreeMock<T extends ExpressionTree, M extends AbstractTreeMock<T, M>> extends AbstractTreeMock<T, M> {
    ExpressionTreeMock(T tree, Context ctx) {
        super(tree, ctx);
    }
}

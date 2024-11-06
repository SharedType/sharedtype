package io.github.cuzfrog.sharedtype.processor.context;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class NewClassTreeMock extends AbstractTreeMock<NewClassTree, NewClassTreeMock> {
    NewClassTreeMock(Context ctx) {
        super(mock(NewClassTree.class), ctx);
    }

    @SafeVarargs
    public final <T extends ExpressionTree, M extends AbstractTreeMock<T, M>> NewClassTreeMock withArguments(ExpressionTreeMock<T, M>... arguments) {
        when(tree.getArguments()).then(invoc -> Arrays.stream(arguments).map(arg -> arg.tree).collect(Collectors.toList()));
        return this;
    }
}

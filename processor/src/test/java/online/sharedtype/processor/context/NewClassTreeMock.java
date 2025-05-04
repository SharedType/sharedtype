package online.sharedtype.processor.context;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class NewClassTreeMock extends ExpressionTreeMock<NewClassTree, NewClassTreeMock> {
    NewClassTreeMock(Context ctx) {
        super(mock(NewClassTree.class), ctx);
        when(tree.getKind()).thenReturn(NewClassTree.Kind.NEW_CLASS);
    }

    public NewClassTreeMock withArguments(ExpressionTree... arguments) {
        when(tree.getArguments()).then(invoc -> Arrays.stream(arguments).collect(Collectors.toList()));
        return this;
    }

    public NewClassTreeMock withIdentifier(ExpressionTree identifier) {
        when(tree.getIdentifier()).thenReturn(identifier);
        return this;
    }
}

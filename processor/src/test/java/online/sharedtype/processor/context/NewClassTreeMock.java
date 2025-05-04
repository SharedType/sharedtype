package online.sharedtype.processor.context;

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

    public NewClassTreeMock withArguments(ExpressionTree... arguments) {
        when(tree.getArguments()).then(invoc -> Arrays.stream(arguments).collect(Collectors.toList()));
        return this;
    }

    public NewClassTreeMock withIdentifier(ExpressionTree identifier) {
        when(tree.getIdentifier()).thenReturn(identifier);
        return this;
    }
}

package online.sharedtype.processor.context;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;

import javax.lang.model.element.Name;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class IdentifierTreeMock extends ExpressionTreeMock<IdentifierTree, IdentifierTreeMock> {
    IdentifierTreeMock(String name, Context ctx) {
        super(mock(IdentifierTree.class, String.format("Tree(%s)", name)), ctx);
        Name elementName = new MockName(name);
        when(tree.getName()).thenReturn(elementName);
        when(tree.getKind()).thenReturn(Tree.Kind.IDENTIFIER);
    }
}

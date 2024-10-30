package org.sharedtype.processor.context;

import com.sun.source.tree.IdentifierTree;

import javax.lang.model.element.Name;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class IdentifierTreeMock extends ExpressionTreeMock<IdentifierTree, IdentifierTreeMock> {
    IdentifierTreeMock(String name, Context ctx) {
        super(mock(IdentifierTree.class, String.format("Tree(%s)", name)), ctx);
        Name elementName = mock(Name.class);
        when(tree.getName()).thenReturn(elementName);
        when(elementName.toString()).thenReturn(name);
    }
}

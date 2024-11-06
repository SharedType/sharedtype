package online.sharedtype.processor.context;

import com.sun.source.tree.Tree;

import javax.lang.model.element.Element;

import static org.mockito.Mockito.when;

public abstract class AbstractTreeMock<T extends Tree, M extends AbstractTreeMock<T, M>> {
    final Context ctx;
    final T tree;

    AbstractTreeMock(T tree, Context ctx) {
        this.ctx = ctx;
        this.tree = tree;
    }

    void fromElement(Element element) {
        when(ctx.getTrees().getTree(element)).thenReturn(tree);
    }
}

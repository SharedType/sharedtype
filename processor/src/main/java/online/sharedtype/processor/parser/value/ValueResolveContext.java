package online.sharedtype.processor.parser.value;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import lombok.Builder;
import lombok.Getter;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@Getter
@Builder(toBuilder = true)
final class ValueResolveContext {
    // utils:
    private final Trees trees;
    private final Elements elements;
    private final Types types;

    // original values:
    private final Element fieldElement;
    private final TypeElement ctxTypeElement;

    // current values:
    private final Tree tree;
    private final TypeElement enclosingTypeElement;

    Scope getScope() {
        return trees.getScope(trees.getPath(enclosingTypeElement));
    }

    BlockTree getStaticBlock() {
        for (Tree member : trees.getTree(enclosingTypeElement).getMembers()) {
            if (member.getKind() == Tree.Kind.BLOCK) {
                BlockTree blockTree = (BlockTree) member;
                if (blockTree.isStatic()) {
                    return blockTree;
                }
            }
        }
        throw new SharedTypeException("No static block found for type: " + enclosingTypeElement);
    }

    PackageElement getPackageElement() {
        return elements.getPackageOf(enclosingTypeElement);
    }
}

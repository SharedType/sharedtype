package online.sharedtype.processor.parser.value;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@EqualsAndHashCode(of = {"fieldElement", "tree", "enclosingTypeElement"})
@Getter
final class ValueResolveContext {
    // utils:
    private final Context ctx;
    private final Trees trees;
    private final Elements elements;
    private final Types types;

    // original values:
    private final Element fieldElement;

    // current values:
    private final Tree tree;
    private final TypeElement enclosingTypeElement;

    ValueResolveContext(Context ctx, Element fieldElement, Tree tree, TypeElement enclosingTypeElement) {
        this.ctx = ctx;
        this.trees = ctx.getTrees();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        this.types = ctx.getProcessingEnv().getTypeUtils();
        this.fieldElement = fieldElement;
        this.tree = tree;
        this.enclosingTypeElement = enclosingTypeElement;
    }
    public static Builder builder(Context ctx) {
        return new Builder(ctx);
    }

    Scope getScope() {
        TreePath treePath = trees.getPath(enclosingTypeElement);
        return treePath == null ? null : trees.getScope(treePath);
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

    public Builder toBuilder() {
        return new Builder(this.ctx).fieldElement(this.fieldElement).tree(this.tree).enclosingTypeElement(this.enclosingTypeElement);
    }

    @ToString
    @RequiredArgsConstructor
    static final class Builder {
        private final Context ctx;
        private Element fieldElement;
        private Tree tree;
        private TypeElement enclosingTypeElement;

        public Builder fieldElement(Element fieldElement) {
            this.fieldElement = fieldElement;
            return this;
        }
        public Builder tree(Tree tree) {
            this.tree = tree;
            return this;
        }
        public Builder enclosingTypeElement(TypeElement enclosingTypeElement) {
            this.enclosingTypeElement = enclosingTypeElement;
            return this;
        }
        public ValueResolveContext build() {
            return new ValueResolveContext(this.ctx, this.fieldElement, this.tree, this.enclosingTypeElement);
        }
    }
}

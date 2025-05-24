package online.sharedtype.processor.parser.value;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import lombok.experimental.UtilityClass;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import online.sharedtype.processor.support.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
final class ValueResolveUtils {
    private final static Set<String> TO_FIND_ENCLOSED_ELEMENT_KIND_SET = new HashSet<>(6);
    static {
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.ENUM.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.CLASS.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.INTERFACE.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add("RECORD");
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.FIELD.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.ENUM_CONSTANT.name());
    }

    @Nullable
    static TypeElement getEnclosingTypeElement(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement instanceof TypeElement) {
            return (TypeElement) enclosingElement;
        }
        return null;
    }
    @Nullable
    static ExpressionTree getValueTree(Tree tree, TypeElement enclosingTypeElement) {
        final ExpressionTree valueTree;
        if (tree instanceof LiteralTree) {
            valueTree = (LiteralTree) tree;
        } else if (tree instanceof VariableTree) {
            valueTree = ((VariableTree) tree).getInitializer();
        } else if (tree instanceof AssignmentTree) {
            valueTree = ((AssignmentTree) tree).getExpression();
        } else if (tree instanceof IdentifierTree) {
            valueTree = (IdentifierTree) tree;
        } else if (tree instanceof MemberSelectTree) {
            valueTree = (MemberSelectTree) tree;
        } else {
            throw new SharedTypeInternalError(String.format(
                "Not supported tree type for constant field parsing. Field: '%s' of kind '%s' and type '%s' in '%s'",
                tree, tree.getKind(), tree.getClass().getSimpleName(), enclosingTypeElement
            ));
        }
        return valueTree;
    }

    @Nullable
    static Element findElementInLocalScope(@Nullable Scope scope, String name, TypeElement enclosingTypeElement) {
        Element referencedElement = findElementInTree(scope, name);
        if (referencedElement == null) { // find local scope references that cannot be found via tree
            // no need to check all enclosing elements, since constants are only parsed in the directly annotated type
            referencedElement = findEnclosedElement(enclosingTypeElement, name);
        }
        return referencedElement;
    }

    @Nullable
    static Element findEnclosedElement(Element enclosingElement, String name) {
        for (Element element : enclosingElement.getEnclosedElements()) {
            if (element.getSimpleName().contentEquals(name)) {
                if (!TO_FIND_ENCLOSED_ELEMENT_KIND_SET.contains(element.getKind().name())) {
                    throw new SharedTypeException(String.format(
                        "Enclosed field '%s' is of element kind '%s': element '%s' in '%s', which is not supported. Supported element kinds: %s.",
                        name, element.getKind(), element, enclosingElement, String.join(", ", TO_FIND_ENCLOSED_ELEMENT_KIND_SET)));
                }
                return element;
            }
        }
        return null;
    }

    /**
     * Only element in the file scope, not including package private or inherited.
     */
    @Nullable
    private static Element findElementInTree(Scope scope, String name) {
        Scope curScope = scope;
        while (curScope != null) {
            for (Element element : curScope.getLocalElements()) {
                if (element.getSimpleName().contentEquals(name) && TO_FIND_ENCLOSED_ELEMENT_KIND_SET.contains(element.getKind().name())) {
                    return element;
                }
            }
            curScope = curScope.getEnclosingScope();
        }
        return null;
    }
}

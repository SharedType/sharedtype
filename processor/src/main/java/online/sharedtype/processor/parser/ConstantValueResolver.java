package online.sharedtype.processor.parser;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.Trees;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
final class ConstantValueResolver {
    private final static Set<String> TO_FIND_ENCLOSED_ELEMENT_KIND_SET = new HashSet<>(4);

    static {
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.ENUM.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.CLASS.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.INTERFACE.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add("RECORD");
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.FIELD.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.ENUM_CONSTANT.name());
    }

    private final Context ctx;
    private final Trees trees;
    private final Elements elements;
    private final Types types;
    /**
     * original context field to resolve the value for
     */
    private final Element fieldElement;
    private final TypeElement ctxTypeElement;
    private final Tree tree;

    ConstantValueResolver(Context ctx, Element fieldElement, TypeElement ctxTypeElement) {
        this.ctx = ctx;
        this.fieldElement = fieldElement;
        this.ctxTypeElement = ctxTypeElement;
        this.trees = ctx.getTrees();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        this.types = ctx.getProcessingEnv().getTypeUtils();
        this.tree = ctx.getTrees().getTree(fieldElement);
        if (tree == null) {
            ctx.error(fieldElement, "Cannot parse constant value for field: %s in %s, tree is null from the field element. " +
                    "If the type is from a dependency jar/compiled class file, tree is not available at the time of annotation processing. " +
                    "Check if the type or its custom mapping is correct.",
                fieldElement, ctxTypeElement);
        }
    }

    Object resolve() {
        if (tree == null) {
            return null;
        }
        return recursivelyResolveConstantValue(tree, ctxTypeElement);
    }

    /**
     * @param tree the tree representing the value, it can reference to another element
     * @return resolved constant value
     */
    private Object recursivelyResolveConstantValue(Tree tree, TypeElement enclosingTypeElement) {
        try {
            ExpressionTree valueTree = getValueTree(tree, enclosingTypeElement);
            if (valueTree instanceof LiteralTree) {
                return ((LiteralTree) valueTree).getValue();
            }
            if (valueTree == null && tree.getKind() == Tree.Kind.VARIABLE) {
                VariableTree variableTree = (VariableTree) tree;
                return resolveEvaluationInStaticBlock(variableTree.getName(), enclosingTypeElement);
            }
            if (valueTree == null) {
                throw new SharedTypeException(String.format("Cannot find value for tree: '%s' in '%s'", tree, enclosingTypeElement));
            }

            Scope scope = ctx.getTrees().getScope(ctx.getTrees().getPath(enclosingTypeElement));
            Element referencedFieldElement = recursivelyResolveReferencedElement(valueTree, scope, enclosingTypeElement);
            if (referencedFieldElement != null) {
                TypeElement referencedFieldEnclosingTypeElement = getEnclosingTypeElement(referencedFieldElement);
                if (referencedFieldEnclosingTypeElement == null) {
                    throw new SharedTypeException(String.format("Cannot find enclosing type for field: '%s'", referencedFieldElement));
                }
                return recursivelyResolveConstantValue(trees.getTree(referencedFieldElement), referencedFieldEnclosingTypeElement);
            }
        } catch (SharedTypeException e) {
            ctx.error(fieldElement, e.getMessage());
        }
        ctx.error(fieldElement, "Only literal value is supported for constant field." +
                " Field tree: %s in %s. Consider to ignore this field or exclude constants generation for this type.",
            tree, ctxTypeElement);
        return null;
    }

    @Nullable
    private static ExpressionTree getValueTree(Tree tree, TypeElement enclosingTypeElement) {
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

    private Object resolveEvaluationInStaticBlock(Name variableName, TypeElement enclosingTypeElement) {
        BlockTree blockTree = getStaticBlock(enclosingTypeElement);
        for (StatementTree statement : blockTree.getStatements()) {
            if (statement.getKind() == Tree.Kind.EXPRESSION_STATEMENT) {
                ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) statement;
                ExpressionTree expressionTree = expressionStatementTree.getExpression();
                if (expressionTree.getKind() == Tree.Kind.ASSIGNMENT) {
                    AssignmentTree assignmentTree = (AssignmentTree) expressionTree;
                    ExpressionTree variableTree = assignmentTree.getVariable();
                    if (variableTree instanceof IdentifierTree) {
                        IdentifierTree identifierTree = (IdentifierTree) variableTree;
                        if (variableName.contentEquals(identifierTree.getName())) {
                            return recursivelyResolveConstantValue(assignmentTree.getExpression(), enclosingTypeElement);
                        }
                    }
                }
            }
        }
        throw new SharedTypeException("No static evaluation found for constant field: " + fieldElement + " in " + enclosingTypeElement);
    }

    private BlockTree getStaticBlock(TypeElement enclosingTypeElement) {
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

    private Element recursivelyResolveReferencedElement(ExpressionTree valueTree, Scope scope, TypeElement enclosingTypeElement) {
        if (valueTree instanceof IdentifierTree) {
            IdentifierTree identifierTree = (IdentifierTree) valueTree;
            String name = identifierTree.getName().toString();
            Element referencedElement = findElementInLocalScope(scope, name, enclosingTypeElement);
            if (referencedElement == null) { // find package scope references
                referencedElement = findEnclosedElement(elements.getPackageOf(enclosingTypeElement), name);
            }
            if (referencedElement == null) {
                referencedElement = findElementInInheritedScope(enclosingTypeElement, name);
            }
            return referencedElement;
        }
        if (valueTree instanceof MemberSelectTree) {
            MemberSelectTree memberSelectTree = (MemberSelectTree) valueTree;
            ExpressionTree expressionTree = memberSelectTree.getExpression();
            Element selecteeElement = recursivelyResolveReferencedElement(expressionTree, scope, enclosingTypeElement);
            if (!(selecteeElement instanceof TypeElement)) {
                throw new SharedTypeException(String.format(
                    "A selectee element must be typeElement, but found: %s in %s", selecteeElement, enclosingTypeElement));
            }
            return findEnclosedElement(selecteeElement, memberSelectTree.getIdentifier().toString());
        }
        return null;
    }

    private static Element findElementInLocalScope(Scope scope, String name, TypeElement enclosingTypeElement) {
        Element referencedElement = findElementInTree(scope, name);
        if (referencedElement == null) { // find local scope references that cannot be found via tree
            // no need to check all enclosing elements, since constants are only parsed in the directly annotated type
            referencedElement = findEnclosedElement(enclosingTypeElement, name);
        }
        return referencedElement;
    }

    @Nullable
    private Element findElementInInheritedScope(TypeElement enclosingTypeElement, String name) {
        TypeElement curEnclosingTypeElement = enclosingTypeElement;
        while (curEnclosingTypeElement != null) {
            Element referencedElement = findEnclosedElement(types.asElement(curEnclosingTypeElement.getSuperclass()), name);
            if (referencedElement != null) {
                return referencedElement;
            }
            curEnclosingTypeElement = getEnclosingTypeElement(curEnclosingTypeElement);
        }
        return null;
    }

    @Nullable
    private static Element findEnclosedElement(Element enclosingElement, String name) {
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

    @Nullable
    private static TypeElement getEnclosingTypeElement(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement instanceof TypeElement) {
            return (TypeElement) enclosingElement;
        }
        return null;
    }
}

package online.sharedtype.processor.parser.value;

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
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
final class ConstantValueResolver implements ValueResolver {
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
    private final ValueResolver enumValueResolver;

    @Override
    public ValueHolder resolve(Element fieldElement, TypeElement ctxTypeElement) {
        Tree tree = ctx.getTrees().getTree(fieldElement);
        if (tree == null) {
            ctx.error(fieldElement, "Cannot parse constant value for field: %s in %s, tree is null from the field element. " +
                    "If the type is from a dependency jar/compiled class file, tree is not available at the time of annotation processing. " +
                    "Check if the type or its custom mapping is correct.",
                fieldElement, ctxTypeElement);
            return ValueHolder.NULL;
        }
        try {
            ValueResolveContext parsingContext = ValueResolveContext.builder()
                .trees(ctx.getTrees()).elements(ctx.getProcessingEnv().getElementUtils()).types(ctx.getProcessingEnv().getTypeUtils())
                .fieldElement(fieldElement)
                .tree(tree).enclosingTypeElement(ctxTypeElement)
                .build();
            return ValueHolder.of(recursivelyResolveConstantValue(parsingContext));
        } catch (SharedTypeException e) {
            ctx.error(fieldElement, "Failed to resolve constant value. " +
                    "Field tree: %s in %s. Consider to ignore this field or exclude constants generation for this type. " +
                    "Error message: %s",
                tree, ctxTypeElement, e.getMessage());
        }
        return ValueHolder.NULL;
    }

    private Object recursivelyResolveConstantValue(ValueResolveContext parsingContext) {
        ExpressionTree valueTree = getValueTree(parsingContext);
        if (valueTree instanceof LiteralTree) {
            return ((LiteralTree) valueTree).getValue();
        }

        Tree tree = parsingContext.getTree();
        TypeElement enclosingTypeElement = parsingContext.getEnclosingTypeElement();
        if (valueTree == null && tree.getKind() == Tree.Kind.VARIABLE) {
            VariableTree variableTree = (VariableTree) tree;
            return resolveEvaluationInStaticBlock(variableTree.getName(), parsingContext);
        }
        if (valueTree == null) {
            throw new SharedTypeException(String.format("Cannot find value for tree: '%s' in '%s'", tree, enclosingTypeElement));
        }

        Element referencedFieldElement = recursivelyResolveReferencedElement(valueTree, parsingContext);
        if (referencedFieldElement == null) {
            throw new SharedTypeException(String.format("Cannot find referenced field for tree: '%s' in '%s'", tree, enclosingTypeElement));
        }
        TypeElement referencedFieldEnclosingTypeElement = ValueResolveUtils.getEnclosingTypeElement(referencedFieldElement);
        if (referencedFieldEnclosingTypeElement == null) {
            throw new SharedTypeException(String.format("Cannot find enclosing type for field: '%s'", referencedFieldElement));
        }
        if (referencedFieldElement.getKind() == ElementKind.ENUM_CONSTANT) {
            return enumValueResolver.resolve(referencedFieldElement, referencedFieldEnclosingTypeElement);
        }

        ValueResolveContext nextParsingContext = parsingContext.toBuilder()
            .tree(parsingContext.getTrees().getTree(referencedFieldElement)).enclosingTypeElement(referencedFieldEnclosingTypeElement)
            .build();
        return recursivelyResolveConstantValue(nextParsingContext);
    }

    @Nullable
    private static ExpressionTree getValueTree(ValueResolveContext parsingContext) {
        final ExpressionTree valueTree;
        Tree tree = parsingContext.getTree();
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
                tree, tree.getKind(), tree.getClass().getSimpleName(), parsingContext.getEnclosingTypeElement()
            ));
        }
        return valueTree;
    }

    private Object resolveEvaluationInStaticBlock(Name variableName, ValueResolveContext parsingContext) {
        BlockTree blockTree = parsingContext.getStaticBlock();
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
                            ValueResolveContext nextParsingContext = parsingContext.toBuilder().tree(assignmentTree.getExpression()).build();
                            return recursivelyResolveConstantValue(nextParsingContext);
                        }
                    }
                }
            }
        }
        throw new SharedTypeException(String.format(
            "No static evaluation found for constant field: %s in %s", parsingContext.getFieldElement(), parsingContext.getEnclosingTypeElement()));
    }

    private static Element recursivelyResolveReferencedElement(ExpressionTree valueTree, ValueResolveContext parsingContext) {
        Scope scope = parsingContext.getScope();
        TypeElement enclosingTypeElement = parsingContext.getEnclosingTypeElement();
        if (valueTree instanceof IdentifierTree) {
            IdentifierTree identifierTree = (IdentifierTree) valueTree;
            String name = identifierTree.getName().toString();
            Element referencedElement = findElementInLocalScope(scope, name, enclosingTypeElement);
            if (referencedElement == null) { // find package scope references
                referencedElement = findEnclosedElement(parsingContext.getPackageElement(), name);
            }
            if (referencedElement == null) {
                referencedElement = findElementInInheritedScope(name, parsingContext);
            }
            return referencedElement;
        }
        if (valueTree instanceof MemberSelectTree) {
            MemberSelectTree memberSelectTree = (MemberSelectTree) valueTree;
            ExpressionTree expressionTree = memberSelectTree.getExpression();
            Element selecteeElement = recursivelyResolveReferencedElement(expressionTree, parsingContext);
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
    private static Element findElementInInheritedScope(String name, ValueResolveContext parsingContext) {
        TypeElement curEnclosingTypeElement = parsingContext.getEnclosingTypeElement();
        while (curEnclosingTypeElement != null) {
            Element referencedElement = findEnclosedElement(parsingContext.getTypes().asElement(curEnclosingTypeElement.getSuperclass()), name);
            if (referencedElement != null) {
                return referencedElement;
            }
            curEnclosingTypeElement = ValueResolveUtils.getEnclosingTypeElement(curEnclosingTypeElement);
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
}

package online.sharedtype.processor.parser.value;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import java.util.List;
import java.util.Objects;

import static online.sharedtype.processor.domain.Constants.MATH_TYPE_QUALIFIED_NAMES;
import static online.sharedtype.processor.parser.value.ValueResolveUtils.findElementInLocalScope;
import static online.sharedtype.processor.parser.value.ValueResolveUtils.findEnclosedElement;

@RequiredArgsConstructor
final class ValueResolverBackendImpl implements ValueResolverBackend {
    private final ValueParser valueParser;

    @Override
    public Object recursivelyResolve(ValueResolveContext parsingContext) {
        ExpressionTree valueTree = ValueResolveUtils.getValueTree(parsingContext.getTree(), parsingContext.getEnclosingTypeElement());
        if (valueTree instanceof LiteralTree) {
            return ((LiteralTree) valueTree).getValue();
        }
        if (valueTree instanceof NewClassTree) {
            NewClassTree newClassTree = (NewClassTree) valueTree;
            return resolveMathTypeNewClassValue(newClassTree, parsingContext);
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
            return valueParser.resolve(referencedFieldElement, referencedFieldEnclosingTypeElement);
        }

        ValueResolveContext nextParsingContext = parsingContext.toBuilder()
            .tree(parsingContext.getTrees().getTree(referencedFieldElement)).enclosingTypeElement(referencedFieldEnclosingTypeElement)
            .build();
        return recursivelyResolve(nextParsingContext);
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
                            return recursivelyResolve(nextParsingContext);
                        }
                    }
                }
            }
        }
        throw new SharedTypeException(String.format(
            "No static evaluation found for constant field: %s in %s", parsingContext.getFieldElement(), parsingContext.getEnclosingTypeElement()));
    }

    private String resolveMathTypeNewClassValue(NewClassTree newClassTree, ValueResolveContext parsingContext) {
        ExpressionTree typeIdentifierTree = newClassTree.getIdentifier();
        Element referencedElement = recursivelyResolveReferencedElement(typeIdentifierTree, parsingContext);
        if (!(referencedElement instanceof TypeElement)) {
            throw new SharedTypeException(String.format(
                "A new class type element must be typeElement, but found: %s in %s, new class tree: %s",
                referencedElement, parsingContext.getEnclosingTypeElement(), typeIdentifierTree));
        }

        TypeElement typeElement = (TypeElement) referencedElement;
        if (!MATH_TYPE_QUALIFIED_NAMES.contains(typeElement.getQualifiedName().toString())) {
            throw new SharedTypeException(String.format("Math type must be one of %s, but found: %s", MATH_TYPE_QUALIFIED_NAMES, typeElement));
        }

        List<? extends ExpressionTree> arguments = newClassTree.getArguments();
        if (arguments.size() != 1) {
            throw new SharedTypeException(String.format("Math type constructor must have only one argument: %s", newClassTree));
        }
        ExpressionTree argument = arguments.get(0);

        ValueResolveContext nextParsingContext = parsingContext.toBuilder()
            .tree(argument).enclosingTypeElement(typeElement).build();
        return Objects.toString(recursivelyResolve(nextParsingContext));
    }

    private static Element recursivelyResolveReferencedElement(ExpressionTree valueTree, ValueResolveContext parsingContext) {
        TypeElement enclosingTypeElement = parsingContext.getEnclosingTypeElement();
        if (valueTree instanceof IdentifierTree) {
            IdentifierTree identifierTree = (IdentifierTree) valueTree;
            String name = identifierTree.getName().toString();
            Scope scope = parsingContext.getScope();
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
}

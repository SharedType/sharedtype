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
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.DependingKind;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.parser.type.TypeContext;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.support.annotation.VisibleForTesting;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
final class ConstantTypeDefParser implements TypeDefParser {
    private static final Set<String> SUPPORTED_ELEMENT_KIND = new HashSet<>(4);
    private final static Set<String> TO_FIND_ENCLOSED_ELEMENT_KIND_SET = new HashSet<>(4);
    static {
        SUPPORTED_ELEMENT_KIND.add(ElementKind.CLASS.name());
        SUPPORTED_ELEMENT_KIND.add(ElementKind.INTERFACE.name());
        SUPPORTED_ELEMENT_KIND.add("RECORD");
        SUPPORTED_ELEMENT_KIND.add(ElementKind.ENUM.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.ENUM.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.CLASS.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.INTERFACE.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add("RECORD");
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.FIELD.name());
        TO_FIND_ENCLOSED_ELEMENT_KIND_SET.add(ElementKind.ENUM_CONSTANT.name());
    }

    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (!SUPPORTED_ELEMENT_KIND.contains(typeElement.getKind().name())) {
            return Collections.emptyList();
        }
        if (ctx.getTrees() == null) {
            ctx.info(typeElement, "Skip parsing constants for type %s, because tree is not available.", typeElement);
            return Collections.emptyList();
        }

        String qualifiedName = typeElement.getQualifiedName().toString();
        List<TypeDef> cachedDefs = ctx.getTypeStore().getTypeDefs(qualifiedName);
        if (cachedDefs == null || cachedDefs.isEmpty()) {
            throw new SharedTypeInternalError("No main type def found for: " + qualifiedName);
        }
        TypeDef mainTypeDef = cachedDefs.get(0);
        if (shouldSkip(mainTypeDef)) {
            return Collections.emptyList();
        }

        Config config = ctx.getTypeStore().getConfig(mainTypeDef);
        if (!config.includes(SharedType.ComponentType.CONSTANTS)) {
            return Collections.emptyList();
        }
        ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder().element(typeElement)
            .qualifiedName(qualifiedName)
            .simpleName(config.getSimpleName())
            .build();

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (ctx.isIgnored(enclosedElement)) {
                continue;
            }

            if (enclosedElement.getKind() == ElementKind.FIELD && enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                ConstantField constantField = new ConstantField(
                    enclosedElement.getSimpleName().toString(),
                    typeInfoParser.parse(enclosedElement.asType(), TypeContext.builder().typeDef(constantNamespaceDef).dependingKind(DependingKind.COMPONENTS).build()),
                    resolveValue(enclosedElement, typeElement)
                );
                constantNamespaceDef.components().add(constantField);
            }
        }
        if (constantNamespaceDef.components().isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(constantNamespaceDef);
    }

    private static boolean shouldSkip(TypeDef mainTypeDef) {
        if (mainTypeDef instanceof ClassDef) {
            ClassDef classDef = (ClassDef) mainTypeDef;
            return classDef.isMapType() || !classDef.isAnnotated();
        }
        return false;
    }

    @VisibleForTesting
    Object resolveValue(Element fieldElement, TypeElement ctxTypeElement) {
        Tree tree = ctx.getTrees().getTree(fieldElement);
        if (tree == null) {
            ctx.error(fieldElement, "Cannot parse constant value for field: %s in %s, tree is null from the field element. " +
                    "If the type is from a dependency jar/compiled class file, tree is not available at the time of annotation processing. " +
                    "Check if the type or its custom mapping is correct.",
                fieldElement, ctxTypeElement);
            return null;
        }
        try {
            ParsingContext parsingContext = ParsingContext.builder()
                .trees(ctx.getTrees()).elements(ctx.getProcessingEnv().getElementUtils()).types(ctx.getProcessingEnv().getTypeUtils())
                .fieldElement(fieldElement)
                .tree(tree).enclosingTypeElement(ctxTypeElement)
                .build();
            return recursivelyResolveConstantValue(parsingContext);
        } catch (SharedTypeException e) {
            ctx.error(fieldElement, "Failed to resolve constant value. " +
                    "Field tree: %s in %s. Consider to ignore this field or exclude constants generation for this type. " +
                    "Error message: %s",
                tree, ctxTypeElement, e.getMessage());
        }
        return null;
    }

    private static Object recursivelyResolveConstantValue(ParsingContext parsingContext) {
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
        if (referencedFieldElement != null) {
            TypeElement referencedFieldEnclosingTypeElement = getEnclosingTypeElement(referencedFieldElement);
            if (referencedFieldEnclosingTypeElement == null) {
                throw new SharedTypeException(String.format("Cannot find enclosing type for field: '%s'", referencedFieldElement));
            }
            ParsingContext nextParsingContext = parsingContext.toBuilder()
                .tree(parsingContext.getTrees().getTree(referencedFieldElement)).enclosingTypeElement(referencedFieldEnclosingTypeElement)
                .build();
            return recursivelyResolveConstantValue(nextParsingContext);
        }
        return null;
    }

    @Nullable
    private static ExpressionTree getValueTree(ParsingContext parsingContext) {
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

    private static Object resolveEvaluationInStaticBlock(Name variableName, ParsingContext parsingContext) {
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
                            ParsingContext nextParsingContext = parsingContext.toBuilder().tree(assignmentTree.getExpression()).build();
                            return recursivelyResolveConstantValue(nextParsingContext);
                        }
                    }
                }
            }
        }
        throw new SharedTypeException(String.format(
            "No static evaluation found for constant field: %s in %s", parsingContext.getFieldElement(), parsingContext.getEnclosingTypeElement()));
    }

    private static Element recursivelyResolveReferencedElement(ExpressionTree valueTree, ParsingContext parsingContext) {
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
    private static Element findElementInInheritedScope(String name, ParsingContext parsingContext) {
        TypeElement curEnclosingTypeElement = parsingContext.getEnclosingTypeElement();
        while (curEnclosingTypeElement != null) {
            Element referencedElement = findEnclosedElement(parsingContext.getTypes().asElement(curEnclosingTypeElement.getSuperclass()), name);
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

    @Getter
    @Builder(toBuilder = true)
    static final class ParsingContext {
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
}

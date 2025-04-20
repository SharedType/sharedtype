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
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    }

    private final Context ctx;
    private final TypeInfoParser typeInfoParser;
    private final Trees trees;
    private final Elements elements;
    private final Types types;
    ConstantTypeDefParser(Context ctx, TypeInfoParser typeInfoParser) {
        this.ctx = ctx;
        this.typeInfoParser = typeInfoParser;
        this.trees = ctx.getTrees();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        this.types = ctx.getProcessingEnv().getTypeUtils();
    }

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
                Tree tree = ctx.getTrees().getTree(enclosedElement);
                if (tree == null) {
                    throw new SharedTypeInternalError(String.format("Cannot parse constant value for field: %s in %s, tree is null from the field element. " +
                            "If the type is from a dependency jar/compiled class file, tree is not available at the time of annotation processing. " +
                            "Check if the type or its custom mapping is correct.",
                        enclosedElement.getSimpleName(), typeElement));
                }
                Scope scope = ctx.getTrees().getScope(ctx.getTrees().getPath(enclosedElement));
                ConstantField constantField = new ConstantField(
                    enclosedElement.getSimpleName().toString(),
                    typeInfoParser.parse(enclosedElement.asType(), TypeContext.builder().typeDef(constantNamespaceDef).dependingKind(DependingKind.COMPONENTS).build()),
                    parseConstantValue(enclosedElement, tree, scope, typeElement)
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

    private Object parseConstantValue(Element fieldElement, Tree tree, Scope scope, TypeElement enclosingTypeElement) {
        if (tree instanceof LiteralTree) {
            return ((LiteralTree) tree).getValue();
        }
        try {
            ExpressionTree valueTree = getValueTree(tree, enclosingTypeElement);
            if (valueTree instanceof LiteralTree) {
                return ((LiteralTree) valueTree).getValue();
            }
            if (valueTree == null) {
                return resolveEvaluationInStaticBlock(fieldElement, scope, enclosingTypeElement);
            }

            Element referencedFieldElement = recursivelyResolveReferencedElement(valueTree, scope, enclosingTypeElement);
            if (referencedFieldElement != null) {
                return parseConstantValue(referencedFieldElement, trees.getTree(referencedFieldElement), scope, enclosingTypeElement);
            }
        } catch (SharedTypeException e) {
            ctx.error(fieldElement, e.getMessage());
        }
        ctx.error(enclosingTypeElement, "Only literal value is supported for constant field." +
                " Field: %s in %s. Consider use @SharedType.Ignore to ignore this field or exclude constants generation for this type.",
            tree, enclosingTypeElement.getQualifiedName());
        return null;
    }

    @Nullable
    private static ExpressionTree getValueTree(Tree tree, TypeElement enclosingTypeElement) {
        final ExpressionTree valueTree;
        if (tree instanceof VariableTree) {
            valueTree = ((VariableTree) tree).getInitializer();
        } else if (tree instanceof AssignmentTree) {
            valueTree = ((AssignmentTree) tree).getExpression();
        } else if (tree instanceof IdentifierTree) {
            valueTree = (IdentifierTree) tree;
        } else if (tree instanceof MemberSelectTree) {
            valueTree = (MemberSelectTree) tree;
        } else {
            throw new SharedTypeException(String.format(
                "Only VariableTree or AssignmentTree is supported for constant field. Field: %s in %s",
                tree, enclosingTypeElement
            ));
        }
        return valueTree;
    }

    private Object resolveEvaluationInStaticBlock(Element fieldElement, Scope scope, TypeElement enclosingTypeElement) {
        BlockTree blockTree = getStaticBlock(enclosingTypeElement);
        for (StatementTree statement : blockTree.getStatements()) {
            if (statement.getKind() == Tree.Kind.EXPRESSION_STATEMENT) {
                ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) statement;
                ExpressionTree expressionTree = expressionStatementTree.getExpression();
                if (expressionTree.getKind() == Tree.Kind.ASSIGNMENT) {
                    AssignmentTree assignmentTree = (AssignmentTree) expressionTree;
                    Element referencedVariableElement = recursivelyResolveReferencedElement(assignmentTree.getVariable(), scope, enclosingTypeElement);

                    if (referencedVariableElement.equals(fieldElement)) {
                        return parseConstantValue(fieldElement, assignmentTree.getExpression(), scope, enclosingTypeElement);
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
            Element referencedElement = findElementInTree(scope, identifierTree.getName().toString());
            if (referencedElement == null) {
                referencedElement = findEnclosedElement(enclosingTypeElement, identifierTree.getName().toString());
            }
            if (referencedElement == null) {
                referencedElement = findEnclosedElement(elements.getPackageOf(enclosingTypeElement), identifierTree.getName().toString());
            }
            if (referencedElement == null) {
                referencedElement = findEnclosedElement(types.asElement(enclosingTypeElement.getSuperclass()), identifierTree.getName().toString());
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

    @Nullable
    private Element findEnclosedElement(Element enclosingElement, String name) {
        for (Element element : enclosingElement.getEnclosedElements()) {
            if (element.getSimpleName().contentEquals(name)) {
                if (!TO_FIND_ENCLOSED_ELEMENT_KIND_SET.contains(element.getKind().name())) {
                    ctx.error(enclosingElement,
                        "Constant field '%s' is referencing a %s element: %s in %s, which is not supported. Supported element kinds: %s.",
                        name, element.getSimpleName(), enclosingElement, String.join(", ", TO_FIND_ENCLOSED_ELEMENT_KIND_SET));
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

package online.sharedtype.processor.parser.value;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.support.annotation.VisibleForTesting;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

import static online.sharedtype.processor.support.utils.Utils.allInstanceFields;

@RequiredArgsConstructor
final class EnumValueResolver implements ValueResolver {
    private final Context ctx;

    @Override
    public Object resolve(Element enumConstantElement, TypeElement enumTypeElement) {
        VariableElement enumConstant = (VariableElement) enumConstantElement;
        int ctorArgIdx = resolveCtorIndex(enumTypeElement);
        if (ctorArgIdx < 0) {
            return enumConstant.getSimpleName().toString();
        }

        Tree tree = ctx.getTrees().getTree(enumConstant);
        if (tree instanceof VariableTree) {
            VariableTree variableTree = (VariableTree) tree;
            return resolveValue(enumTypeElement, variableTree, ctorArgIdx);
        } else if (tree == null) {
            ctx.error(enumConstant, "Literal value cannot be parsed from enum constant: %s of enum %s, because source tree from the element is null." +
                    " This could mean at the time of the annotation processing, the source tree was not available." +
                    " Is this class from a dependency jar/compiled class file? Please refer to the documentation for more information.",
                enumConstant, enumTypeElement);
        } else {
            throw new SharedTypeInternalError(String.format(
                "Unsupported tree during parsing enum %s, kind: %s, tree: %s, element: %s", enumTypeElement, tree.getKind(), tree, enumConstant));
        }
        return null;
    }

    private Object resolveValue(TypeElement enumTypeElement, VariableTree tree, int ctorArgIdx) {
        ExpressionTree init = tree.getInitializer();
        if (init instanceof NewClassTree) {
            NewClassTree newClassTree = (NewClassTree) init;
            try {
                ExpressionTree argTree = newClassTree.getArguments().get(ctorArgIdx);
                if (argTree instanceof LiteralTree) {
                    LiteralTree argLiteralTree = (LiteralTree) argTree;
                    return argLiteralTree.getValue();
                } else {
                    ctx.error(enumTypeElement, "Unsupported argument in enum type %s: %s in %s, argIndex: %s. Only literals are supported as enum value.",
                        enumTypeElement, argTree, tree, ctorArgIdx);
                    return null;
                }
            } catch (IndexOutOfBoundsException e) {
                throw new SharedTypeInternalError(String.format(
                    "Initializer in enum %s has incorrect number of arguments: %s in tree: %s, argIndex: %s", enumTypeElement, init, tree, ctorArgIdx));
            }
        }
        throw new SharedTypeInternalError(String.format("Unsupported initializer in enum %s: %s in tree: %s", enumTypeElement, init, tree));
    }

    @VisibleForTesting
    int resolveCtorIndex(TypeElement enumTypeElement) {
        Integer cachedIdx = ctx.getTypeStore().getEnumValueIndex(enumTypeElement.getQualifiedName().toString());
        if (cachedIdx != null) {
            return cachedIdx;
        }

        List<VariableElement> instanceFields = allInstanceFields(enumTypeElement);
        int idx = -1;
        for (int i = 0; i < instanceFields.size(); i++) {
            VariableElement field = instanceFields.get(i);
            if (ctx.isAnnotatedAsEnumValue(field)) {
                if (idx != -1) {
                    ctx.error(field, "Multiple enum values in enum %s, only one field can be annotated as enum value.", enumTypeElement);
                }
                idx = i;
            }
        }
        ctx.getTypeStore().saveEnumValueIndex(enumTypeElement.getQualifiedName().toString(), idx);
        return idx;
    }
}

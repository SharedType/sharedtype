package online.sharedtype.processor.parser.value;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.EnumCtorIndex;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.support.annotation.VisibleForTesting;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

import static online.sharedtype.processor.support.utils.Utils.allInstanceFields;

@RequiredArgsConstructor
final class EnumValueParser implements ValueParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;
    private final ValueResolverBackend valueResolverBackend;

    @Override
    public ValueHolder resolve(Element enumConstantElement, TypeElement enumTypeElement) {
        VariableElement enumConstant = (VariableElement) enumConstantElement;
        String enumConstantName = enumConstant.getSimpleName().toString();
        EnumCtorIndex ctorArgIdx = resolveCtorIndex(enumTypeElement);
        if (ctorArgIdx == EnumCtorIndex.NONE) {
            ConcreteTypeInfo typeInfo = (ConcreteTypeInfo) typeInfoParser.parse(enumTypeElement.asType(), enumTypeElement);
            return ValueHolder.ofEnum(enumConstantName, typeInfo, enumConstantName);
        }

        Tree tree = ctx.getTrees().getTree(enumConstant);
        if (tree instanceof VariableTree) {
            VariableTree variableTree = (VariableTree) tree;
            Object value = resolveValue(enumTypeElement, enumTypeElement, variableTree, ctorArgIdx.getIdx());
            ConcreteTypeInfo valueType = null;
            while (value instanceof ValueHolder) {
                ValueHolder valueHolder = (ValueHolder) value;
                value = valueHolder.getValue();
                valueType = valueHolder.getValueType();
            }
            if (valueType == null) {
                TypeInfo fieldTypeInfo = typeInfoParser.parse(ctorArgIdx.getField().asType(), enumTypeElement);
                if (fieldTypeInfo instanceof ConcreteTypeInfo) {
                    valueType = (ConcreteTypeInfo) fieldTypeInfo;
                } else {
                    ctx.error(enumConstant, "Complex field types are not supported for value resolving. Only literal types or references are supported," +
                        " the type is '%s'.", ctorArgIdx.getField().asType());
                    return ValueHolder.NULL;
                }
            }
            return ValueHolder.ofEnum(enumConstantName, valueType, value);
        } else if (tree == null) {
            ctx.error(enumConstant, "Literal value cannot be parsed from enum constant: %s of enum %s, because source tree from the element is null." +
                    " This could mean at the time of the annotation processing, the source tree was not available." +
                    " Is this class from a dependency jar/compiled class file? Please refer to the documentation for more information.",
                enumConstant, enumTypeElement);
        } else {
            throw new SharedTypeInternalError(String.format(
                "Unsupported tree during parsing enum %s, kind: %s, tree: %s, element: %s", enumTypeElement, tree.getKind(), tree, enumConstant));
        }
        return ValueHolder.NULL;
    }

    private Object resolveValue(Element enumConstantElement, TypeElement enumTypeElement, VariableTree tree, int ctorArgIdx) {
        ExpressionTree init = tree.getInitializer();
        if (init instanceof NewClassTree) {
            NewClassTree newClassTree = (NewClassTree) init;
            try {
                ExpressionTree argTree = newClassTree.getArguments().get(ctorArgIdx);
                ValueResolveContext valueResolveContext = ValueResolveContext.builder(ctx)
                    .enclosingTypeElement(enumTypeElement)
                    .fieldElement(enumConstantElement)
                    .tree(argTree)
                    .build();
                return valueResolverBackend.recursivelyResolve(valueResolveContext);
            } catch (IndexOutOfBoundsException e) {
                throw new SharedTypeInternalError(String.format(
                    "Initializer in enum %s has incorrect number of arguments: %s in tree: %s, argIndex: %s", enumTypeElement, init, tree, ctorArgIdx));
            } catch (SharedTypeException e) {
                ctx.error(enumConstantElement, "Failed to parse argument value at index %s. Tree: %s Error message: %s",
                    ctorArgIdx, tree, e.getMessage());
                return null;
            }
        }
        throw new SharedTypeInternalError(String.format("Unsupported initializer in enum %s: %s in tree: %s", enumTypeElement, init, tree));
    }

    @VisibleForTesting
    EnumCtorIndex resolveCtorIndex(TypeElement enumTypeElement) {
        EnumCtorIndex cachedIdx = ctx.getTypeStore().getEnumValueIndex(enumTypeElement.getQualifiedName().toString());
        if (cachedIdx != null) {
            return cachedIdx;
        }

        List<VariableElement> instanceFields = allInstanceFields(enumTypeElement);
        EnumCtorIndex idx = EnumCtorIndex.NONE;
        for (int i = 0; i < instanceFields.size(); i++) {
            VariableElement field = instanceFields.get(i);
            if (ctx.isAnnotatedAsEnumValue(field)) {
                if (idx != EnumCtorIndex.NONE) {
                    ctx.error(field, "Multiple enum values in enum %s, only one field can be annotated as enum value.", enumTypeElement);
                }
                idx = new EnumCtorIndex(i, field);
            }
        }
        ctx.getTypeStore().saveEnumValueIndex(enumTypeElement.getQualifiedName().toString(), idx);
        return idx;
    }
}

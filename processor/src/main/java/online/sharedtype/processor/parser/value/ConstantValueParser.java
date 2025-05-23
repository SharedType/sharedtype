package online.sharedtype.processor.parser.value;

import com.sun.source.tree.Tree;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@RequiredArgsConstructor
final class ConstantValueParser implements ValueParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;
    private final ValueResolverBackend valueResolverBackend;

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
            ValueResolveContext parsingContext = ValueResolveContext.builder(ctx)
                .fieldElement(fieldElement)
                .tree(tree).enclosingTypeElement(ctxTypeElement)
                .build();
            TypeInfo fieldTypeInfo = typeInfoParser.parse(fieldElement.asType(), ctxTypeElement);
            if (fieldTypeInfo instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo valueType = (ConcreteTypeInfo) fieldTypeInfo;
                return ValueHolder.of(valueType, valueResolverBackend.recursivelyResolve(parsingContext));
            }
            ctx.error(fieldElement, "Complex field types are not supported for value resolving. Only literal types or references are supported," +
                    " the type is '%s'.", fieldElement.asType());
            return ValueHolder.NULL;
        } catch (SharedTypeException e) {
            ctx.error(fieldElement, "Failed to parse constant value. " +
                    "Field tree: '%s' in '%s'. Consider to ignore this field or exclude constants generation for this type. " +
                    "Error message: %s",
                tree, ctxTypeElement, e.getMessage());
        }
        return ValueHolder.NULL;
    }
}

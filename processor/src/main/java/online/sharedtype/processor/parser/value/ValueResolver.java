package online.sharedtype.processor.parser.value;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public interface ValueResolver {
    ValueHolder resolve(Element element, TypeElement ctxTypeElement);

    static ValueResolver create(Context ctx, TypeInfoParser typeInfoParser) {
        CompositeValueResolver compositeValueResolver = new CompositeValueResolver();
        ValueResolverBackend backend = new ValueResolverBackendImpl(compositeValueResolver);
        compositeValueResolver.registerResolver(ElementKind.ENUM_CONSTANT, new EnumValueResolver(ctx, typeInfoParser, backend));
        compositeValueResolver.registerResolver(ElementKind.FIELD, new ConstantValueResolver(ctx, backend));
        return compositeValueResolver;
    }
}

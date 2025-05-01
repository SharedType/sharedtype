package online.sharedtype.processor.parser.value;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public interface ValueParser {
    ValueHolder resolve(Element element, TypeElement ctxTypeElement);

    static ValueParser create(Context ctx, TypeInfoParser typeInfoParser) {
        CompositeValueParser compositeValueResolver = new CompositeValueParser();
        ValueResolverBackend backend = new ValueResolverBackendImpl(compositeValueResolver);
        compositeValueResolver.registerResolver(ElementKind.ENUM_CONSTANT, new EnumValueParser(ctx, typeInfoParser, backend));
        compositeValueResolver.registerResolver(ElementKind.FIELD, new ConstantValueParser(ctx, typeInfoParser, backend));
        return compositeValueResolver;
    }
}

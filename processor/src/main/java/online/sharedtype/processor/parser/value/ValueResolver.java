package online.sharedtype.processor.parser.value;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.EnumMap;
import java.util.Map;

public interface ValueResolver {
    ValueHolder resolve(Element element, TypeElement ctxTypeElement);

    static ValueResolver create(Context ctx, TypeInfoParser typeInfoParser) {
        Map<ElementKind, ValueResolver> valueResolvers = new EnumMap<>(ElementKind.class);
        ValueResolver enumValueResolver = new EnumValueResolver(ctx, typeInfoParser);
        valueResolvers.put(ElementKind.ENUM_CONSTANT, enumValueResolver);
        valueResolvers.put(ElementKind.FIELD, new ConstantValueResolver(ctx, enumValueResolver));
        return new CompositeValueResolver(valueResolvers);
    }
}

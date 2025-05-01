package online.sharedtype.processor.parser.value;

import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.EnumMap;
import java.util.Map;

final class CompositeValueParser implements ValueParser {
    private final Map<ElementKind, ValueParser> resolvers = new EnumMap<>(ElementKind.class);

    void registerResolver(ElementKind kind, ValueParser resolver) {
        resolvers.put(kind, resolver);
    }

    @Override
    public ValueHolder resolve(Element element, TypeElement ctxTypeElement) {
        ValueParser resolver = resolvers.get(element.getKind());
        if (resolver != null) {
            return resolver.resolve(element, ctxTypeElement);
        }
        throw new SharedTypeInternalError(String.format("Unable to resolve value for element '%s', unsupported element kind: %s", element, element.getKind()));
    }
}

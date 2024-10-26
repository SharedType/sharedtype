package org.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import org.sharedtype.processor.context.Context;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class CompositeTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final Map<ElementKind, TypeDefParser> parsers;

    @Override
    public TypeDef parse(TypeElement typeElement) {
        if (ctx.isTypeIgnored(typeElement)) {
            return null;
        }
        String qualifiedName = typeElement.getQualifiedName().toString();
        var cachedDef = ctx.getTypeCache().getTypeDef(qualifiedName);
        if (cachedDef != null) {
            return cachedDef;
        }
        ctx.info("Processing: " + typeElement.getQualifiedName());
        var parser = parsers.get(typeElement.getKind());
        if (parser == null) {
            throw new SharedTypeInternalError(String.format("Unsupported element: %s, kind=%s", typeElement, typeElement.getKind()));
        }

        var typeDef = parser.parse(typeElement);
        ctx.getTypeCache().saveTypeDef(qualifiedName, typeDef);
        return typeDef;
    }
}

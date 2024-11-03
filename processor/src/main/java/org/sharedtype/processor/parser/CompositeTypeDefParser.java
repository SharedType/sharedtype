package org.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.TypeElement;
import java.util.Map;

@RequiredArgsConstructor
final class CompositeTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final Map<String, TypeDefParser> parsers;

    @Override
    public TypeDef parse(TypeElement typeElement) {
        if (ctx.isTypeIgnored(typeElement)) {
            return null;
        }
        String qualifiedName = typeElement.getQualifiedName().toString();
        TypeDef cachedDef = ctx.getTypeCache().getTypeDef(qualifiedName);
        if (cachedDef != null) {
            return cachedDef;
        }
        ctx.info("Processing: " + typeElement.getQualifiedName());
        TypeDefParser parser = parsers.get(typeElement.getKind().name());
        if (parser == null) {
            throw new SharedTypeInternalError(String.format("Unsupported element: %s, kind=%s", typeElement, typeElement.getKind()));
        }

        TypeDef typeDef = parser.parse(typeElement);
        ctx.getTypeCache().saveTypeDef(qualifiedName, typeDef);
        return typeDef;
    }
}

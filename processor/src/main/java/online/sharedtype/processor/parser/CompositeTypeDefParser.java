package online.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class CompositeTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final List<TypeDefParser> parsers;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (ctx.isIgnored(typeElement)) {
            return Collections.emptyList();
        }
        String qualifiedName = typeElement.getQualifiedName().toString();
        List<TypeDef> cachedDef = ctx.getTypeStore().getTypeDefs(qualifiedName);
        if (cachedDef != null) {
            return new ArrayList<>(cachedDef);
        }

        if (ctx.isArraylike(typeElement.asType())) {
            ctx.warn(typeElement, "Type '%s' is an array type, which cannot be parsed and emitted as a standalone type.", typeElement.getQualifiedName());
            return Collections.emptyList();
        }
        if (ctx.isDatetimelike(typeElement.asType())) {
            ctx.warn(typeElement, "Type '%s' is a datetime type, which cannot be parsed and emitted as a standalone type.", typeElement.getQualifiedName());
            return Collections.emptyList();
        }
        // TODO: warn for maplikeType

        ctx.info("Processing: %s", typeElement.getQualifiedName());
        List<TypeDef> typeDefs = new ArrayList<>();
        for (TypeDefParser typeDefParser : parsers) {
            List<TypeDef> parsedTypeDefs = typeDefParser.parse(typeElement);
            for (TypeDef parsedTypeDef : parsedTypeDefs) {
                ctx.getTypeStore().saveTypeDef(qualifiedName, parsedTypeDef);
            }
            typeDefs.addAll(parsedTypeDefs);
        }
        return typeDefs;
    }
}

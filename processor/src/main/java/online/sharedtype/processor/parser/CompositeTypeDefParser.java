package online.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class CompositeTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final List<TypeDefParser> parsers;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (ctx.isTypeIgnored(typeElement)) {
            return Collections.emptyList();
        }
        String qualifiedName = typeElement.getQualifiedName().toString();
        List<TypeDef> cachedDef = ctx.getTypeStore().getTypeDefs(qualifiedName);
        if (cachedDef != null) {
            return new ArrayList<>(cachedDef);
        }
        ctx.info("Processing: " + typeElement.getQualifiedName());
        List<TypeDef> typeDefs = new ArrayList<>();
        for (TypeDefParser typeDefParser : parsers) {
            typeDefs.addAll(typeDefParser.parse(typeElement));
        }

        for (TypeDef typeDef : typeDefs) {
            if (typeElement.getAnnotation(SharedType.class) != null) {
                typeDef.setAnnotated(true);
            }
            ctx.getTypeStore().saveTypeDef(qualifiedName, typeDef);
        }
        return typeDefs;
    }
}

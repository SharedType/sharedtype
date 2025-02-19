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
 *
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
        ctx.info("Processing: " + typeElement.getQualifiedName());
        List<TypeDef> typeDefs = new ArrayList<>();
        for (TypeDefParser typeDefParser : parsers) {
            List<TypeDef> parsedTypeDefs = typeDefParser.parse(typeElement);
            for (TypeDef parsedTypeDef : parsedTypeDefs) {
                ctx.getTypeStore().saveTypeDef(qualifiedName, parsedTypeDef);
            }
            typeDefs.addAll(parsedTypeDefs);
        }

        for (TypeDef typeDef : typeDefs) {
            if (typeElement.getAnnotation(SharedType.class) != null) {
                typeDef.setAnnotated(true);
            }
        }
        return typeDefs;
    }
}

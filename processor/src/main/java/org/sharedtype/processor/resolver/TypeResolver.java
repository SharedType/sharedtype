package org.sharedtype.processor.resolver;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.TypeDefParser;

import java.util.List;

public interface TypeResolver {
    List<TypeDef> resolve(List<TypeDef> typeDefs);

    static TypeResolver create(Context ctx, TypeDefParser typeDefParser) {
        return new LoopTypeResolver(ctx, typeDefParser);
    }
}

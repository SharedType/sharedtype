package org.sharedtype.processor.resolver;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.TypeDefParser;

import java.util.List;

/**
 * Resolve required but unknown type information after initial parsing stage.
 *
 * @see TypeDefParser
 * @author Cause Chung
 */
public interface TypeResolver {
    /**
     * Resolve {@link org.sharedtype.domain.TypeInfo} by traversing all types.
     *
     * @param typeDefs the types discovered in initial parsing stage, they are types directly annotated with {@link org.sharedtype.annotation.SharedType}.
     * @return all type definitions needed to generate output. Including dependency types, e.g. referenced types, super types.
     */
    List<TypeDef> resolve(List<TypeDef> typeDefs);

    static TypeResolver create(Context ctx, TypeDefParser typeDefParser) {
        return new LoopTypeResolver(ctx, typeDefParser);
    }
}

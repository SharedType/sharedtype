package online.sharedtype.processor.resolver;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.parser.TypeDefParser;

import java.util.List;

/**
 * Resolve required but unknown type information after initial parsing stage.
 *
 * @see TypeDefParser
 * @author Cause Chung
 */
public interface TypeResolver {
    /**
     * Resolve {@link TypeInfo} by traversing all types.
     *
     * @param typeDefs the types discovered in initial parsing stage, they are types directly annotated with {@link SharedType}.
     * @return all type definitions needed to generate output. Including dependency types, e.g. referenced types, super types.
     */
    List<TypeDef> resolve(List<TypeDef> typeDefs);

    static TypeResolver create(Context ctx, TypeDefParser typeDefParser) {
        return new LoopTypeResolver(ctx, typeDefParser);
    }
}

package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.def.TypeDef;

import java.util.Arrays;
import java.util.List;

final class CompositeTypeResolver implements TypeResolver {
    private final List<TypeResolver> resolvers;

    public CompositeTypeResolver(TypeResolver... resolvers) {
        this.resolvers = Arrays.asList(resolvers);
    }

    @Override
    public List<TypeDef> resolve(List<TypeDef> typeDefs) {
        List<TypeDef> result = typeDefs;
        for (TypeResolver resolver : resolvers) {
            result = resolver.resolve(result);
        }
        return result;
    }
}

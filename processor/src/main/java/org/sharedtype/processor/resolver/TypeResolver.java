package org.sharedtype.processor.resolver;

import java.util.List;

import dagger.Module;
import org.sharedtype.processor.domain.TypeDef;

@Module
public interface TypeResolver {
    List<TypeDef> resolve(List<TypeDef> typeDefs);
}
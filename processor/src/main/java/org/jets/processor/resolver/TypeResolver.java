package org.jets.processor.resolver;

import java.util.List;

import dagger.Module;
import org.jets.processor.domain.TypeDef;

@Module
public interface TypeResolver {
    List<TypeDef> resolve(List<TypeDef> typeDefs);
}

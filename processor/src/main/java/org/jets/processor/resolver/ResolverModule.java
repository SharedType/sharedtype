package org.jets.processor.resolver;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ResolverModule {
    @Binds abstract TypeResolver bindTypeResolver(LoopTypeResolver loopTypeResolver);
}

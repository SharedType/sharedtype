package org.jets.processor.parser.mapper;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TypeMapperModule {
    @Binds abstract TypeMapper bindTypeMapper(TypescriptTypeMapper typescriptTypeMapper);
}

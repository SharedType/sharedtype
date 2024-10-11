package org.jets.processor.parser.type;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TypeMapperModule {
    @Binds abstract TypeMapper bindTypeMapper(TypescriptTypeMapper typescriptTypeMapper);
}

package org.jets.processor.parser.field;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TypeParserModule {
    @Binds abstract FieldElementParser bindTypeMapper(TypescriptFieldElementParser typescriptTypeMapper);
}

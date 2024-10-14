package org.sharedtype.processor.parser.field;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TypeParserModule {
    @Binds abstract VariableElementParser bindTypeMapper(TypescriptVariableElementParser typescriptTypeMapper);
}

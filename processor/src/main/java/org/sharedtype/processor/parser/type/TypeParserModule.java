package org.sharedtype.processor.parser.type;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TypeParserModule {
    @Binds abstract TypeInfoParser bindTypeMapper(TypeInfoParserImpl typescriptTypeMapper);
}

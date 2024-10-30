package org.sharedtype.processor;

import dagger.BindsInstance;
import dagger.Component;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.ParserModule;
import org.sharedtype.processor.parser.TypeDefParser;
import org.sharedtype.processor.resolver.ResolverModule;
import org.sharedtype.processor.resolver.TypeResolver;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ParserModule.class, ResolverModule.class})
interface Components {
    TypeDefParser parser();
    TypeResolver resolver();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder withContext(Context ctx);
        Components build();
    }
}

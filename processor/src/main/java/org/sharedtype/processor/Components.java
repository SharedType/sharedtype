package org.sharedtype.processor;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.ParserModule;
import org.sharedtype.processor.parser.TypeElementParser;
import org.sharedtype.processor.resolver.ResolverModule;
import org.sharedtype.processor.resolver.TypeResolver;
import org.sharedtype.processor.writer.TypeWriter;
import org.sharedtype.processor.writer.WriterModule;

@Singleton
@Component(modules = {ParserModule.class, ResolverModule.class, WriterModule.class})
interface Components {
    TypeElementParser parser();
    TypeResolver resolver();
    TypeWriter writer();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder withContext(Context ctx);
        Components build();
    }
}

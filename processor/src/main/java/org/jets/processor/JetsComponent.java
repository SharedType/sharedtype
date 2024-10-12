package org.jets.processor;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import org.jets.processor.context.GlobalContext;
import org.jets.processor.parser.ParserModule;
import org.jets.processor.parser.TypeElementParser;
import org.jets.processor.resolver.ResolverModule;
import org.jets.processor.resolver.TypeResolver;
import org.jets.processor.writer.TypeWriter;
import org.jets.processor.writer.WriterModule;

@Singleton
@Component(modules = {ParserModule.class, ResolverModule.class, WriterModule.class})
interface JetsComponent {
    TypeElementParser parser();
    TypeResolver resolver();
    TypeWriter writer();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder withContext(GlobalContext ctx);
        JetsComponent build();
    }
}

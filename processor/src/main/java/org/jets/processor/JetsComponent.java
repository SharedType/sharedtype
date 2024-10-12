package org.jets.processor;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import org.jets.processor.context.GlobalContext;
import org.jets.processor.parser.ParserModule;
import org.jets.processor.parser.TypeElementParser;
import org.jets.processor.writer.TypeWriter;
import org.jets.processor.writer.WriterModule;

@Singleton
@Component(modules = {ParserModule.class, WriterModule.class})
interface JetsComponent {
    TypeElementParser parser();
    TypeWriter writer();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder withContext(GlobalContext ctx);
        JetsComponent build();
    }
}

package org.jets.processor;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import org.jets.processor.parser.ParserModule;
import org.jets.processor.parser.TypeElementParser;

@Singleton
@Component(modules = ParserModule.class)
interface JetsComponent {
    TypeElementParser parser();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder withContext(GlobalContext ctx);
        JetsComponent build();
    }
}

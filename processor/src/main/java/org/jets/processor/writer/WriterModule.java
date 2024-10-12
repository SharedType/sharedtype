package org.jets.processor.writer;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class WriterModule {
    @Binds abstract TypeWriter bindTypeWriter(ConsoleDebugWriter typescriptTypeWriter);
}

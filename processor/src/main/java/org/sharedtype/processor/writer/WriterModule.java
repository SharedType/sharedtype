package org.sharedtype.processor.writer;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import org.sharedtype.processor.writer.render.TemplateRenderModule;

@Module(includes = {TemplateRenderModule.class})
public abstract class WriterModule {
    @Binds @IntoSet abstract TypeWriter bindConsoleWriter(ConsoleWriter consoleWriter);
    @Binds @IntoSet abstract TypeWriter bindJavaSerializationFileWriter(JavaSerializationFileWriter javaSerializationFileWriter);
    @Binds @IntoSet abstract TypeWriter bindTypescriptFileWriter(TypescriptTypeFileWriter typescriptTypeFileWriter);

    @Binds abstract TypeWriter bindTypeWriter(CompositeWriter compositeWriter);
}

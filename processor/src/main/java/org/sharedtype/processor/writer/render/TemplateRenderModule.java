package org.sharedtype.processor.writer.render;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class TemplateRenderModule {
    @Binds abstract TemplateRenderer bindTemplateRenderer(MustacheTemplateRenderer mustacheTemplateRenderer);
    @Provides static MustacheFactory provideMustacheFactory() { return new DefaultMustacheFactory(); }
}

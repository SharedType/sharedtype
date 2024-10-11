package org.jets.processor;

import javax.inject.Singleton;

import dagger.Component;
import org.jets.processor.parser.ParserModule;
import org.jets.processor.parser.TypeElementParser;

@Singleton
@Component(modules = ParserModule.class)
public interface JetsComponent {
    TypeElementParser parser();
}

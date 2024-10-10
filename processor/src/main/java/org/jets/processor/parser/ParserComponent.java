package org.jets.processor.parser;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ParserModule.class)
public interface ParserComponent {
    TypeElementParser parser();
}

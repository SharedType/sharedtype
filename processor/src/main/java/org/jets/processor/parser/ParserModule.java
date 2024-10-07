package org.jets.processor.parser;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import java.util.Map;

@Module
interface ParserModule {
    @Provides @Singleton
    static TypeElementParser bindTypeElementParser() {
        return new CompositeTypeElementParser(Map.of(
                ElementKind.RECORD, new JavaRecordParser()
        ));
    }
}

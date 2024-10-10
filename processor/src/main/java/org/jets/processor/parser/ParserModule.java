package org.jets.processor.parser;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import org.jets.processor.dagger.ElementKindKey;

import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import java.util.Map;

@Module
interface ParserModule {
    @Binds @IntoMap @ElementKindKey(ElementKind.RECORD)
    TypeElementParser parser(JavaRecordParser javaRecordParser);
    @Binds
    TypeElementParser bindTypeElementParser(CompositeTypeElementParser compositeTypeElementParser);
}

package org.jets.processor.parser;

import javax.lang.model.element.ElementKind;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import org.jets.processor.parser.field.TypeParserModule;
import org.jets.processor.support.dagger.ElementKindKey;

@Module(includes = TypeParserModule.class)
public abstract class ParserModule {
    @Binds @IntoMap @ElementKindKey(ElementKind.RECORD)
    abstract TypeElementParser recordParser(ClassElementParser classElementParser);

    @Binds @IntoMap @ElementKindKey(ElementKind.CLASS)
    abstract TypeElementParser classParser(ClassElementParser classElementParser);

    @Binds
    abstract TypeElementParser bindTypeElementParser(CompositeTypeElementElementParser compositeTypeElementParser);
}

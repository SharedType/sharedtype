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
    abstract TypeElementParser parser(ClassElementParser recordParser);
    @Binds
    abstract TypeElementParser bindTypeElementParser(CompositeTypeElementElementParser compositeTypeElementParser);
}

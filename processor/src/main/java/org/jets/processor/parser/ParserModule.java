package org.jets.processor.parser;

import javax.lang.model.element.ElementKind;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import org.jets.processor.parser.type.TypeMapperModule;
import org.jets.processor.support.dagger.ElementKindKey;

@Module(includes = TypeMapperModule.class)
public abstract class ParserModule {
    @Binds @IntoMap @ElementKindKey(ElementKind.RECORD)
    abstract TypeElementParser parser(RecordParser recordParser);
    @Binds
    abstract TypeElementParser bindTypeElementParser(CompositeTypeElementParser compositeTypeElementParser);
}

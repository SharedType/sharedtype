package org.jets.processor.parser;

import javax.lang.model.element.ElementKind;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import org.jets.processor.dagger.ElementKindKey;
import org.jets.processor.parser.type.TypeMapperModule;

@Module(includes = TypeMapperModule.class)
interface ParserModule {
    @Binds @IntoMap @ElementKindKey(ElementKind.RECORD)
    TypeElementParser parser(RecordParser recordParser);
    @Binds
    TypeElementParser bindTypeElementParser(CompositeTypeElementParser compositeTypeElementParser);
}

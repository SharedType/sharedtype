package org.sharedtype.processor.parser;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import org.sharedtype.processor.parser.type.TypeParserModule;
import org.sharedtype.processor.support.dagger.ElementKindKey;

import javax.lang.model.element.ElementKind;

@Module(includes = TypeParserModule.class)
public abstract class ParserModule {
    @Binds @IntoMap @ElementKindKey(ElementKind.RECORD)
    abstract TypeElementParser recordParser(ClassElementParser classElementParser);

    @Binds @IntoMap @ElementKindKey(ElementKind.CLASS)
    abstract TypeElementParser classParser(ClassElementParser classElementParser);

    @Binds
    abstract TypeElementParser bindTypeElementParser(CompositeTypeElementElementParser compositeTypeElementParser);
}

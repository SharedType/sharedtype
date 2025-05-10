package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;

abstract class AbstractEnumConverter implements TemplateDataConverter {
    @Override
    public final boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof EnumDef && !((EnumDef) typeDef).components().isEmpty();
    }
}

package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.TypeDef;

abstract class AbstractStructConverter implements TemplateDataConverter {
    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        if (!(typeDef instanceof ClassDef)) {
            return false;
        }
        ClassDef classDef = (ClassDef) typeDef;
        if (classDef.isMapType()) {
            return false;
        }
        return !classDef.components().isEmpty() || classDef.isDepended();
    }
}

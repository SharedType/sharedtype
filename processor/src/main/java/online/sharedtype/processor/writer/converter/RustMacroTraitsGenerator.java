package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.def.TypeDef;

import java.util.Set;

interface RustMacroTraitsGenerator {
    Set<String> generate(TypeDef typeDef);
}

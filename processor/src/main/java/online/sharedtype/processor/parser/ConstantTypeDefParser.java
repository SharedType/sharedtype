package online.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

@RequiredArgsConstructor
final class ConstantTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Nullable
    @Override
    public TypeDef parse(TypeElement typeElement) {
        return null;
    }
}

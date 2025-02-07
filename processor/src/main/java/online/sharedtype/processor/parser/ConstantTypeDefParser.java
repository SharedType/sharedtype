package online.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
final class ConstantTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        return Collections.emptyList();
    }
}

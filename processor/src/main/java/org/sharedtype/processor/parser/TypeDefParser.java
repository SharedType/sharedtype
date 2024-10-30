package org.sharedtype.processor.parser;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

public interface TypeDefParser {
    /**
     * @return null if the typeElement is ignored.
     */
    @Nullable
    TypeDef parse(TypeElement typeElement);

    static TypeDefParser create(Context ctx) {
        TypeInfoParser typeInfoParser = TypeInfoParser.create(ctx);
        Map<ElementKind, TypeDefParser> parsers = new HashMap<>(4);
        parsers.put(ElementKind.CLASS, new ClassTypeDefParser(ctx, typeInfoParser));
        parsers.put(ElementKind.INTERFACE, new ClassTypeDefParser(ctx, typeInfoParser));
        parsers.put(ElementKind.ENUM, new EnumTypeDefParser(ctx, typeInfoParser));
        parsers.put(ElementKind.RECORD, new ClassTypeDefParser(ctx, typeInfoParser));
        return new CompositeTypeDefParser(ctx, parsers);
    }
}

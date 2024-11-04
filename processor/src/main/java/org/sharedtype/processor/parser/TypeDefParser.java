package org.sharedtype.processor.parser;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse type structural information.
 *
 * @see TypeDef
 * @see TypeInfoParser
 * @author Cause Chung
 */
public interface TypeDefParser {
    /**
     * Parse structural information.
     *
     * @return null if the typeElement is ignored or invalid.
     */
    @Nullable
    TypeDef parse(TypeElement typeElement);

    static TypeDefParser create(Context ctx) {
        TypeInfoParser typeInfoParser = TypeInfoParser.create(ctx);
        Map<String, TypeDefParser> parsers = new HashMap<>(4);
        parsers.put(ElementKind.CLASS.name(), new ClassTypeDefParser(ctx, typeInfoParser));
        parsers.put(ElementKind.INTERFACE.name(), new ClassTypeDefParser(ctx, typeInfoParser));
        parsers.put(ElementKind.ENUM.name(), new EnumTypeDefParser(ctx, typeInfoParser));
        parsers.put("RECORD", new ClassTypeDefParser(ctx, typeInfoParser));
        return new CompositeTypeDefParser(ctx, parsers);
    }
}

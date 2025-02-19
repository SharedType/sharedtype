package online.sharedtype.processor.parser;

import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @return empty if the typeElement is ignored or invalid.
     *         A typeElement can be ignored via configuration.
     *         An invalid type can be an unsupported type, e.g. a non-static inner class.
     *         The main classDef or enumDef must be the first element in the list, constantDef should be the 2nd element if exists.
     */
    List<TypeDef> parse(TypeElement typeElement);

    static TypeDefParser create(Context ctx) {
        TypeInfoParser typeInfoParser = TypeInfoParser.create(ctx);
        List<TypeDefParser> parsers = new ArrayList<>(3); // order matters! see #parse
        parsers.add(new ClassTypeDefParser(ctx, typeInfoParser));
        parsers.add(new EnumTypeDefParser(ctx, typeInfoParser));
        parsers.add(new ConstantTypeDefParser(ctx, typeInfoParser));
        return new CompositeTypeDefParser(ctx, parsers);
    }
}

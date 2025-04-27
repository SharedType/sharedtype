package online.sharedtype.processor.parser;

import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.parser.value.ValueResolver;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

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
        ValueResolver valueResolver = ValueResolver.create(ctx, typeInfoParser);
        List<TypeDefParser> parsers = new ArrayList<>(3); // order matters! see #parse
        parsers.add(new ClassTypeDefParser(ctx, typeInfoParser));
        parsers.add(new EnumTypeDefParser(ctx, typeInfoParser, valueResolver));
        parsers.add(new ConstantTypeDefParser(ctx, typeInfoParser, valueResolver));
        return new CompositeTypeDefParser(ctx, parsers);
    }
}

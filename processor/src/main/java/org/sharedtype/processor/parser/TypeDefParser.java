package org.sharedtype.processor.parser;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Map;

public interface TypeDefParser {
    /**
     * @return null if the typeElement is ignored.
     */
    @Nullable
    TypeDef parse(TypeElement typeElement);

    static TypeDefParser create(Context ctx) {
        TypeInfoParser typeInfoParser = TypeInfoParser.create(ctx);
        return new CompositeTypeDefParser(ctx, Map.of(
                ElementKind.CLASS, new ClassTypeDefParser(ctx, typeInfoParser),
                ElementKind.INTERFACE, new ClassTypeDefParser(ctx, typeInfoParser),
                ElementKind.ENUM, new EnumTypeDefParser(ctx, typeInfoParser),
                ElementKind. RECORD, new ClassTypeDefParser(ctx, typeInfoParser)
        ));
    }
}

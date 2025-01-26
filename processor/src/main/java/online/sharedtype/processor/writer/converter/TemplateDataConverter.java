package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.HashSet;
import java.util.Set;

public interface TemplateDataConverter {

    boolean shouldAccept(TypeDef typeDef);

    Tuple<Template, Object> convert(TypeDef typeDef);

    static Set<TemplateDataConverter> typescript(Context ctx) {
        Set<TemplateDataConverter> converters = new HashSet<>(2);
        converters.add(new TypescriptInterfaceConverter(ctx, TypeExpressionConverter.typescript(ctx)));
        converters.add(new TypescriptEnumUnionConverter());
        return converters;
    }

    static Set<TemplateDataConverter> rust(Context ctx) {
        Set<TemplateDataConverter> converters = new HashSet<>(2);
        converters.add(new RustStructConverter(ctx, TypeExpressionConverter.rust(ctx)));
        converters.add(new RustEnumConverter());
        return converters;
    }
}

package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
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
        Set<TemplateDataConverter> converters = new HashSet<>(3);
        TypeExpressionConverter typescriptExprConverter = TypeExpressionConverter.typescript(ctx);
        converters.add(new TypescriptInterfaceConverter(ctx, typescriptExprConverter));
        converters.add(new TypescriptEnumUnionConverter());
        converters.add(new ConstantConverter(ctx, typescriptExprConverter, OutputTarget.TYPESCRIPT));
        return converters;
    }

    static Set<TemplateDataConverter> rust(Context ctx) {
        Set<TemplateDataConverter> converters = new HashSet<>(3);
        TypeExpressionConverter rustExprConverter = TypeExpressionConverter.rust(ctx);
        converters.add(new RustStructConverter(ctx, rustExprConverter));
        converters.add(new RustEnumConverter(ctx));
        converters.add(new ConstantConverter(ctx, rustExprConverter, OutputTarget.RUST));
        return converters;
    }
}

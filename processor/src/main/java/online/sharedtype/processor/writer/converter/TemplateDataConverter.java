package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.def.TypeDef;
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
        converters.add(new TypescriptInterfaceConverter(ctx, TypeExpressionConverter.typescript(ctx)));
        converters.add(new TypescriptEnumConverter(ctx));
        converters.add(new ConstantConverter(ctx, null, OutputTarget.TYPESCRIPT));
        return converters;
    }

    static Set<TemplateDataConverter> rust(Context ctx) {
        RustMacroTraitsGenerator rustMacroTraitsGenerator = new RustMacroTraitsGeneratorImpl(ctx);
        TypeExpressionConverter rustTypeExpressionConverter = TypeExpressionConverter.rust(ctx);
        TypeExpressionConverter rustLiteralTypeExpressionConverter = TypeExpressionConverter.rustLiteral();
        Set<TemplateDataConverter> converters = new HashSet<>(3);
        converters.add(new RustStructConverter(ctx, rustTypeExpressionConverter, rustMacroTraitsGenerator));
        converters.add(new RustEnumConverter(rustLiteralTypeExpressionConverter, rustMacroTraitsGenerator));
        converters.add(new ConstantConverter(ctx, rustLiteralTypeExpressionConverter, OutputTarget.RUST));
        return converters;
    }
}

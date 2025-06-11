package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.HashSet;
import java.util.Set;

public interface TemplateDataConverter {

    boolean shouldAccept(TypeDef typeDef);

    Tuple<Template, AbstractTypeExpr> convert(TypeDef typeDef);

    static Set<TemplateDataConverter> typescript(Context ctx) {
        Set<TemplateDataConverter> converters = new HashSet<>(3);
        converters.add(new TypescriptInterfaceConverter(ctx, TypeExpressionConverter.typescript(ctx)));
        converters.add(new TypescriptEnumConverter(ctx));
        converters.add(new DefaultConstantConverter(ctx, TypeExpressionConverter.nullOp(), SharedType.TargetType.TYPESCRIPT));
        return converters;
    }

    static Set<TemplateDataConverter> go(Context ctx) {
        Set<TemplateDataConverter> converters = new HashSet<>(3);
        TypeExpressionConverter typeExpressionConverter = TypeExpressionConverter.go(ctx);
        converters.add(new GoStructConverter(typeExpressionConverter));
        converters.add(new GoEnumConverter(ctx, typeExpressionConverter));
        converters.add(new DefaultConstantConverter(ctx, typeExpressionConverter, SharedType.TargetType.GO));
        return converters;
    }

    static Set<TemplateDataConverter> rust(Context ctx) {
        RustMacroTraitsGenerator rustMacroTraitsGenerator = new RustMacroTraitsGeneratorImpl(ctx);
        TypeExpressionConverter rustTypeExpressionConverter = TypeExpressionConverter.rust(ctx);
        TypeExpressionConverter rustLiteralTypeExpressionConverter = TypeExpressionConverter.rustLiteral(ctx);
        Set<TemplateDataConverter> converters = new HashSet<>(3);
        converters.add(new RustStructConverter(ctx, rustTypeExpressionConverter, rustMacroTraitsGenerator));
        converters.add(new RustEnumConverter(ctx, rustLiteralTypeExpressionConverter, rustMacroTraitsGenerator));
        converters.add(new RustConstantConverter(ctx, rustLiteralTypeExpressionConverter));
        return converters;
    }
}

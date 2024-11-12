package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.HashSet;
import java.util.Set;

public interface TemplateDataConverter {

    boolean supports(TypeDef typeDef);

    Tuple<Template, Object> convert(TypeDef typeDef);

    static Set<TemplateDataConverter> createConverters(Context ctx) {
        Set<OutputTarget> targets = ctx.getProps().getTargets();
        Set<TemplateDataConverter> converters = new HashSet<>(2);
        if (targets.contains(OutputTarget.TYPESCRIPT)) {
            converters.add(new TypescriptInterfaceConverter(ctx));
            converters.add(new TypescriptEnumUnionConverter());
        }
        if (targets.contains(OutputTarget.RUST)) {
            converters.add(new RustStructConverter());
        }
        return converters;
    }
}

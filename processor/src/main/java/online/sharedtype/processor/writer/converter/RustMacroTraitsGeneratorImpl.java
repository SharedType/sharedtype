package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.def.TypeDef;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

final class RustMacroTraitsGeneratorImpl implements RustMacroTraitsGenerator {
    final Context ctx;
    private final Set<String> defaultTraits;

    RustMacroTraitsGeneratorImpl(Context ctx) {
        this.ctx = ctx;
        this.defaultTraits = ctx.getProps().getRust().getDefaultTypeMacros();
    }

    @Override
    public Set<String> generate(TypeDef typeDef) {
        Config config = ctx.getTypeStore().getConfig(typeDef);
        String[] typeMacroTraits = config.getAnno().rustMacroTraits();
        Set<String> traits = new LinkedHashSet<>(typeMacroTraits.length + defaultTraits.size());
        traits.addAll(defaultTraits);
        Collections.addAll(traits, typeMacroTraits);
        return traits;
    }
}

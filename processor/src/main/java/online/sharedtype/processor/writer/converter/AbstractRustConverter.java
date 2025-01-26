package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

abstract class AbstractRustConverter implements TemplateDataConverter {
    final Context ctx;
    private final Set<String> defaultTraits;

    AbstractRustConverter(Context ctx) {
        this.ctx = ctx;
        this.defaultTraits = ctx.getProps().getRust().getDefaultTypeMacros();
    }

    final Set<String> macroTraits(TypeDef typeDef) {
        Config config = ctx.getTypeStore().getConfig(typeDef);
        String[] typeMacroTraits = config != null ? config.getAnno().rustMacroTraits() : Utils.emptyStringArray();
        Set<String> traits = new LinkedHashSet<>(typeMacroTraits.length + defaultTraits.size());
        traits.addAll(defaultTraits);
        Collections.addAll(traits, typeMacroTraits);
        return traits;
    }

    static String buildMacroTraitsExpr(Set<String> macroTraits) {
        if (macroTraits.isEmpty()) {
            return null;
        }
        return String.format("#[derive(%s)]", String.join(", ", macroTraits));
    }
}

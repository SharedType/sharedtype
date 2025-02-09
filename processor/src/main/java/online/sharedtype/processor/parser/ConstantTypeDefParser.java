package online.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
final class ConstantTypeDefParser implements TypeDefParser {
    private static final Set<String> SUPPORTED_ELEMENT_KIND = new HashSet<String>(4){{
        add(ElementKind.CLASS.name());
        add(ElementKind.INTERFACE.name());
        add("RECORD");
        add(ElementKind.ENUM.name());
    }};
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (!SUPPORTED_ELEMENT_KIND.contains(typeElement.getKind().name())) {
            return Collections.emptyList();
        }

        Config config = ctx.getTypeStore().getConfig(typeElement.getQualifiedName().toString());
        if (config == null) {
            config = new Config(typeElement);
        }

        return Collections.emptyList();
    }
}

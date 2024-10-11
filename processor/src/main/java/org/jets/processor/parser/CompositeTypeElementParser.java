package org.jets.processor.parser;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.GlobalContext;
import org.jets.processor.domain.DefInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class CompositeTypeElementParser implements TypeElementParser {
    private final GlobalContext ctx;
    private final Map<ElementKind, TypeElementParser> parsers;

    @Override
    public List<DefInfo> parse(TypeElement typeElement) {
        ctx.info("Processing: " + typeElement.getQualifiedName());
        var parser = parsers.get(typeElement.getKind());
        if (parser == null) {
            ctx.info("Unsupported element kind: " + typeElement.getKind());
            return null;
        }
        return parser.parse(typeElement);
    }
}

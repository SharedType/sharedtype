package org.jets.processor.parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.Context;
import org.jets.processor.domain.DefInfo;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class CompositeTypeElementParser implements TypeParser {
    private final Context ctx;
    private final Map<ElementKind, TypeParser> parsers;

    @Override
    public List<DefInfo> parse(TypeElement typeElement) {
        ctx.info("Processing: " + typeElement.getQualifiedName());
        var parser = parsers.get(typeElement.getKind());
        if (parser == null) {
            ctx.info("Unsupported element kind: " + typeElement.getKind());
            return Collections.emptyList();
        }
        return parser.parse(typeElement);
    }
}

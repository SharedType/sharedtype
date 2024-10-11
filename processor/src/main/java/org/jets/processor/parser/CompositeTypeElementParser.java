package org.jets.processor.parser;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import lombok.RequiredArgsConstructor;
import org.jets.processor.JetsContext;
import org.jets.processor.domain.ClassInfo;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class CompositeTypeElementParser implements TypeElementParser {
    private final Map<ElementKind, TypeElementParser> parsers;

    @Override
    public ClassInfo parse(TypeElement typeElement, JetsContext ctx) {
        ctx.info("Processing: " + typeElement.getQualifiedName());
        var parser = parsers.get(typeElement.getKind());
        if (parser == null) {
            ctx.info("Unsupported element kind: " + typeElement.getKind());
            return null;
        }
        return parser.parse(typeElement, ctx);
    }
}

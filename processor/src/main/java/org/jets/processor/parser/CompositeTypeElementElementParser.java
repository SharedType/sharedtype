package org.jets.processor.parser;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.Context;
import org.jets.processor.domain.DefInfo;
import org.jets.processor.support.exception.JetsInternalError;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class CompositeTypeElementElementParser implements TypeElementParser {
    private final Context ctx;
    private final Map<ElementKind, TypeElementParser> parsers;

    @Override
    public List<DefInfo> parse(TypeElement typeElement) {
        ctx.info("Processing: " + typeElement.getQualifiedName());
        var parser = parsers.get(typeElement.getKind());
        if (parser == null) {
            throw new JetsInternalError(String.format("Unsupported element: %s, kind=%s", typeElement, typeElement.getKind()));
        }
        return parser.parse(typeElement);
    }
}

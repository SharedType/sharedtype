package org.jets.processor.parser;

import lombok.RequiredArgsConstructor;
import org.jets.processor.JetsContext;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
final class CompositeTypeElementParser implements TypeElementParser {
    private final Map<ElementKind, TypeElementParser> parsers;

    @Override
    public List<TypeInfo> parse(TypeElement typeElement, JetsContext ctx) {
        ctx.log(Diagnostic.Kind.NOTE, "Processing: " + typeElement.getQualifiedName());
        var parser = parsers.get(typeElement.getKind());
        if (parser == null) {
            ctx.log(Diagnostic.Kind.ERROR, "Unsupported element kind: " + typeElement.getKind());
            return Collections.emptyList();
        }
        return parser.parse(typeElement, ctx);
    }
}

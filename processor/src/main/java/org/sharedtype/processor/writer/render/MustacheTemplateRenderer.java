package org.sharedtype.processor.writer.render;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;
import org.sharedtype.processor.support.utils.Tuple;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
final class MustacheTemplateRenderer implements TemplateRenderer {
    private final MustacheFactory mf;
    private final Map<Template, Mustache> compiledTemplates = new HashMap<>();

    @Override
    public void loadTemplates(Template... templates) {
        for (Template template : templates) {
            compiledTemplates.put(template, mf.compile(template.getResourcePath()));
        }
    }

    @Override
    public void render(Writer writer, List<Tuple<Template, Object>> data) {
        for (Tuple<Template, Object> tuple : data) {
            Template template = tuple.a();
            Object values = tuple.b();
            Mustache mustache = compiledTemplates.get(template);
            if (mustache == null) {
                throw new SharedTypeInternalError(String.format("Template not found: '%s'", template));
            }
            mustache.execute(writer, values);
        }
    }
}

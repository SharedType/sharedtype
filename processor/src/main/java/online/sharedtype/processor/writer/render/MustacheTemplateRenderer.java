package online.sharedtype.processor.writer.render;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import online.sharedtype.support.exception.SharedTypeInternalError;
import online.sharedtype.support.utils.Tuple;

import java.io.Writer;
import java.util.List;

/**
 * Implementation via on <a href="https://github.com/spullara/mustache.java">Mustache</a>.
 * The underlying implementation already caches compiled templates.
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class MustacheTemplateRenderer implements TemplateRenderer {
    private final MustacheFactory mf;

    @Override
    public void render(Writer writer, List<Tuple<Template, Object>> data) {
        for (Tuple<Template, Object> tuple : data) {
            Template template = tuple.a();
            Object values = tuple.b();
            Mustache mustache = mf.compile(template.getResourcePath());
            if (mustache == null) {
                throw new SharedTypeInternalError(String.format("Template not found: '%s'", template));
            }
            mustache.execute(writer, values);
        }
    }
}

package online.sharedtype.processor.writer.render;

import com.github.mustachejava.DefaultMustacheFactory;
import online.sharedtype.processor.support.utils.Tuple;

import java.io.Writer;
import java.util.List;

/**
 *
 * @author Cause Chung
 */
public interface TemplateRenderer {

    /**
     * Renders the target output to the writer specified.
     *
     * @param writer java.io.Writer
     * @param data a list of tuple containing the template and corresponding data for rendering.
     */
    void render(Writer writer, List<Tuple<Template, Object>> data);

    static TemplateRenderer create() {
        return new MustacheTemplateRenderer(new DefaultMustacheFactory());
    }
}

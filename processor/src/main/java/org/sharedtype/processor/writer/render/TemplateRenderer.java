package org.sharedtype.processor.writer.render;

import com.github.mustachejava.DefaultMustacheFactory;
import org.sharedtype.support.utils.Tuple;

import java.io.Writer;
import java.util.List;

/**
 *
 * @author Cause Chung
 */
public interface TemplateRenderer {
    /**
     * Implementation can use this method to compile and cache templates.
     * This should be called during initialization of the client writer.
     *
     * @param templates a specific target writer should use.
     */
    void loadTemplates(Template... templates);

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

package org.sharedtype.processor.writer.render;

import org.sharedtype.processor.support.utils.Tuple;

import java.io.Writer;
import java.util.List;

public interface TemplateRenderer {

    void loadTemplates(Template... templates);

    void render(Writer writer, List<Tuple<Template, Object>> data);
}

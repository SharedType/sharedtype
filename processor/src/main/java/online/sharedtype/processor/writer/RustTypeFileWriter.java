package online.sharedtype.processor.writer;

import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.writer.render.Template;
import online.sharedtype.processor.writer.render.TemplateRenderer;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Cause Chung
 */
final class RustTypeFileWriter implements TypeWriter {
    private final TemplateRenderer renderer;

    RustTypeFileWriter(TemplateRenderer renderer) {
        this.renderer = renderer;
        renderer.loadTemplates(
            Template.TEMPLATE_RUST_STRUCT
        );
    }

    @Override
    public void write(List<TypeDef> typeDefs) throws IOException {

    }
}

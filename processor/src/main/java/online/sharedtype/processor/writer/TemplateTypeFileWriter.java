package online.sharedtype.processor.writer;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.writer.converter.TemplateDataConverter;
import online.sharedtype.processor.writer.render.Template;
import online.sharedtype.processor.writer.render.TemplateRenderer;
import online.sharedtype.processor.support.utils.Tuple;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Cause Chung
 */
final class TemplateTypeFileWriter implements TypeWriter {
    private final Context ctx;
    private final TemplateRenderer renderer;
    private final Set<TemplateDataConverter> converters;

    TemplateTypeFileWriter(Context ctx, TemplateRenderer renderer, Set<TemplateDataConverter> converters) {
        this.ctx = ctx;
        this.renderer = renderer;
        this.converters = converters;
    }

    @Override
    public void write(List<TypeDef> typeDefs) throws IOException {
        List<Tuple<Template, Object>> data = new ArrayList<>(typeDefs.size() * converters.size());
        Map<String, TypeDef> simpleNames = new HashMap<>(typeDefs.size());
        for (TypeDef typeDef : typeDefs) {
            TypeDef duplicate = simpleNames.get(typeDef.simpleName());
            if (duplicate != null) {
                ctx.error("Duplicate names found: %s and %s, which is not allowed in output code." +
                    " You may use @SharedType(name=\"...\") to rename a type.", typeDef.qualifiedName(), duplicate.qualifiedName());
                return;
            }
            simpleNames.put(typeDef.simpleName(), typeDef);
            for (TemplateDataConverter converter : converters) {
                data.add(converter.convert(typeDef));
            }
        }

        FileObject file = ctx.createSourceOutput(ctx.getProps().getTypescript().getOutputFileName());
        try (OutputStream outputStream = file.openOutputStream();
             Writer writer = new OutputStreamWriter(outputStream)) {
            renderer.render(writer, data);
        }
    }
}

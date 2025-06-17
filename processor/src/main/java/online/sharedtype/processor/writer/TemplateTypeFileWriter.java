package online.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.writer.adaptor.RenderDataAdaptorFactory;
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
@RequiredArgsConstructor
final class TemplateTypeFileWriter implements TypeWriter {
    private final Context ctx;
    private final TemplateRenderer renderer;
    private final RenderDataAdaptorFactory renderDataAdaptorFactory;
    private final Set<TemplateDataConverter> converters;
    private final String outputFileName;

    @Override
    public void write(List<TypeDef> typeDefs) throws IOException {
        List<Tuple<Template, ?>> data = new ArrayList<>(typeDefs.size() * converters.size());
        data.add(renderDataAdaptorFactory.header(ctx));

        Map<String, TypeDef> simpleNames = new HashMap<>(typeDefs.size());
        for (TypeDef typeDef : typeDefs) {
            TypeDef duplicate = typeDef instanceof ConstantNamespaceDef ? null : simpleNames.get(typeDef.simpleName()); // todo: split class/enum and constant duplication checks
            if (duplicate != null) {
                ctx.warn("Duplicate names found: %s and %s, which may not be valid in output code." +
                    " You may use @SharedType(name=\"...\") to rename a type.", typeDef.qualifiedName(), duplicate.qualifiedName());
            }
            simpleNames.put(typeDef.simpleName(), typeDef);
            for (TemplateDataConverter converter : converters) {
                if (converter.shouldAccept(typeDef)) {
                    data.add(converter.convert(typeDef));
                }
            }
        }

        FileObject file = ctx.createSourceOutput(outputFileName);
        try (OutputStream outputStream = file.openOutputStream();
             Writer writer = new OutputStreamWriter(outputStream)) {
            renderer.render(writer, data);
        }
    }
}

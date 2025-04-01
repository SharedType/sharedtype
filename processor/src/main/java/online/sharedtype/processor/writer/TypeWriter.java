package online.sharedtype.processor.writer;

import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.writer.adaptor.RenderDataAdaptorFactory;
import online.sharedtype.processor.writer.converter.TemplateDataConverter;
import online.sharedtype.processor.writer.render.TemplateRenderer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Writes type meta to target output.
 *
 * @author Cause Chung
 */
public interface TypeWriter {
    /**
     * Writes type meta to target output.
     *
     * @param typeDefs type definitions required to generate output, assumed to be completed.
     * @throws IOException if underlying IO error occurs
     */
    void write(List<TypeDef> typeDefs) throws IOException;

    static TypeWriter create(Context ctx) {
        Set<TypeWriter> writers = new HashSet<>(OutputTarget.values().length);
        if (ctx.getProps().getTargets().contains(OutputTarget.CONSOLE)) {
            writers.add(new ConsoleWriter(ctx));
        }
        if (ctx.getProps().getTargets().contains(OutputTarget.JAVA_SERIALIZED)) {
            writers.add(new JavaSerializationFileWriter(ctx));
        }

        TemplateRenderer renderer = TemplateRenderer.create();
        if (ctx.getProps().getTargets().contains(OutputTarget.TYPESCRIPT)) {
            writers.add(new TemplateTypeFileWriter(
                ctx, renderer, RenderDataAdaptorFactory::typescript, TemplateDataConverter.typescript(ctx), ctx.getProps().getTypescript().getOutputFileName()
            ));
        }
        if (ctx.getProps().getTargets().contains(OutputTarget.RUST)) {
            writers.add(new TemplateTypeFileWriter(
                ctx, renderer, RenderDataAdaptorFactory::rust, TemplateDataConverter.rust(ctx), ctx.getProps().getRust().getOutputFileName()
            ));
        }
        return new CompositeWriter(writers);
    }
}

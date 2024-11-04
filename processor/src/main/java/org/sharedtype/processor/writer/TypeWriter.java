package org.sharedtype.processor.writer;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.context.OutputTarget;
import org.sharedtype.processor.writer.render.TemplateRenderer;

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
            writers.add(new TypescriptTypeFileWriter(ctx, renderer));
        }
        return new CompositeWriter(writers);
    }
}

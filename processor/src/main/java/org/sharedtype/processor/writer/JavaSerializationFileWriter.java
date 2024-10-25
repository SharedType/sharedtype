package org.sharedtype.processor.writer;

import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.TypeDef;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * For internal usage, where integration tests deserilize the generated files back to objects.
 */
@Singleton
final class JavaSerializationFileWriter implements TypeWriter {
    private final Context ctx;
    private final Filer filer;

    @Inject
    JavaSerializationFileWriter(Context ctx) {
        this.ctx = ctx;
        this.filer = ctx.getProcessingEnv().getFiler();
    }

    @Override
    public void write(List<TypeDef> typeDefs) {
        if (ctx.getProps().isJavaSerializationFileWriterEnabled()) {
            try {
                var file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "typeDefs.ser");
                try(var outputStream = file.openOutputStream();
                    var oos = new ObjectOutputStream(outputStream)) {
                    oos.writeObject(typeDefs);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file,", e);
            }
        }
    }
}

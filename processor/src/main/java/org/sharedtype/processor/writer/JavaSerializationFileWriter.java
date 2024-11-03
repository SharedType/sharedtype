package org.sharedtype.processor.writer;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * For internal usage, where integration tests deserialize the generated files back to objects.
 */
final class JavaSerializationFileWriter implements TypeWriter {
    private final Filer filer;

    JavaSerializationFileWriter(Context ctx) {
        this.filer = ctx.getProcessingEnv().getFiler();
    }

    @Override
    public void write(List<TypeDef> typeDefs) {
        try {
            for (TypeDef typeDef : typeDefs) {
                FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", typeDef.qualifiedName() + ".ser");
                try(OutputStream outputStream = file.openOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
                    oos.writeObject(typeDef);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file,", e);
        }
    }
}

package online.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * For internal usage, where integration tests deserialize the generated files back to objects.
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class JavaSerializationFileWriter implements TypeWriter {
    private final Context ctx;

    @Override
    public void write(List<TypeDef> typeDefs) {
        try {
            for (TypeDef typeDef : typeDefs) {
                FileObject file = ctx.createSourceOutput(getTypeName(typeDef) + ".ser");
                try(OutputStream outputStream = file.openOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
                    oos.writeObject(typeDef);
                }
            }
        } catch (IOException e) {
            throw new SharedTypeException("Failed to write to file,", e);
        }
    }

    private String getTypeName(TypeDef typeDef) {
        if (typeDef instanceof ConstantNamespaceDef) {
            return String.format("$%s", typeDef.qualifiedName());
        }
        return typeDef.qualifiedName();
    }
}

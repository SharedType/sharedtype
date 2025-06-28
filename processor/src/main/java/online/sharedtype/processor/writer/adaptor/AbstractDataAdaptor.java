package online.sharedtype.processor.writer.adaptor;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.RenderFlags;
import online.sharedtype.processor.support.annotation.Nullable;
import online.sharedtype.processor.support.exception.SharedTypeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
abstract class AbstractDataAdaptor implements RenderDataAdaptor {
    final Context ctx;

    @SuppressWarnings("unused")
    final RenderFlags renderFlags() {
        return ctx.getRenderFlags();
    }

    abstract String customCodeSnippet();

    static String readCustomCodeSnippet(@Nullable Path customCodePath) {
        if (customCodePath == null) {
            return "";
        }

        if (Files.notExists(customCodePath)) { // this should be checked before calling this method when properties are loaded
            throw new SharedTypeException(String.format("Custom code snippet not found at path '%s'", customCodePath));
        }

        try {
            return new String(Files.readAllBytes(customCodePath));
        } catch (IOException e) {
            throw new SharedTypeException(String.format("Failed to read custom code snippet from path '%s'", customCodePath), e);
        }
    }
}

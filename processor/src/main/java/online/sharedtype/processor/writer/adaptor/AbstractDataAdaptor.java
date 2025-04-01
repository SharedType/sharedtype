package online.sharedtype.processor.writer.adaptor;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.RenderFlags;
import online.sharedtype.processor.support.exception.SharedTypeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
abstract class AbstractDataAdaptor implements RenderDataAdaptor {
    final Context ctx;

    final RenderFlags renderFlags() {
        return ctx.getRenderFlags();
    }

    abstract String customCodeSnippet();

    static String readCustomCodeSnippet(String path) {
        Path customCodePath = Paths.get(path);
        if (Files.notExists(customCodePath)) {
            return "";
        }

        try {
            return new String(Files.readAllBytes(customCodePath));
        } catch (IOException e) {
            throw new SharedTypeException(String.format("Failed to read custom code snippet from path '%s'", customCodePath), e);
        }
    }
}

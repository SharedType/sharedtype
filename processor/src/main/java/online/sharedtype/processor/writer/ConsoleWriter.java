package online.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.context.Context;

import java.util.List;

/**
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class ConsoleWriter implements TypeWriter{
    private final Context ctx;

    @Override
    public void write(List<TypeDef> typeDefs) {
        typeDefs.forEach(d-> ctx.info("Write type: %s%s", System.lineSeparator(), d));
    }
}

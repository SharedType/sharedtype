package io.github.cuzfrog.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import io.github.cuzfrog.sharedtype.domain.TypeDef;
import io.github.cuzfrog.sharedtype.processor.context.Context;

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

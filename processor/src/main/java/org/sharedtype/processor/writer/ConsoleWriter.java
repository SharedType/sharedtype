package org.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class ConsoleWriter implements TypeWriter{
    private final Context ctx;

    @Override
    public void write(List<TypeDef> typeDefs) {
        typeDefs.forEach(d-> ctx.info("Write type: %s%s", System.lineSeparator(), d));
    }
}

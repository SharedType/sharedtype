package io.github.cuzfrog.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import io.github.cuzfrog.sharedtype.domain.TypeDef;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class CompositeWriter implements TypeWriter{
    private final Set<TypeWriter> writers;

    @Override
    public void write(List<TypeDef> typeDefs) throws IOException {
        for (TypeWriter writer : writers) {
            writer.write(typeDefs);
        }
    }
}

package org.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.TypeDef;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Singleton
final class CompositeWriter implements TypeWriter{
    private final Set<TypeWriter> writers;

    @Override
    public void write(List<TypeDef> typeDefs) {
        for (TypeWriter writer : writers) {
            writer.write(typeDefs);
        }
    }
}

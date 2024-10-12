package org.jets.processor.resolver;

import java.util.List;

import dagger.Module;
import org.jets.processor.domain.DefInfo;

@Module
public interface TypeResolver {
    List<DefInfo> resolve(List<DefInfo> typeDefs);
}

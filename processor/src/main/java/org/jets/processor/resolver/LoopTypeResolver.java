package org.jets.processor.resolver;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import org.jets.processor.domain.DefInfo;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class LoopTypeResolver implements TypeResolver {

  @Override
  public List<DefInfo> resolve(List<DefInfo> typeDefs) {
    // TODO
    

    return typeDefs;
  }
  
}

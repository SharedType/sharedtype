package org.jets.processor.writer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.GlobalContext;
import org.jets.processor.domain.DefInfo;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class TypescriptTypeWriter implements TypeWriter {
  private final GlobalContext ctx;

  @Override
  public void write(List<DefInfo> typeDefs) {
    // TODO 
    typeDefs.forEach(d-> ctx.info("Write type: %s", d.name()));
  }

}

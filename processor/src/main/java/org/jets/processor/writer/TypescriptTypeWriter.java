package org.jets.processor.writer;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.Context;
import org.jets.processor.domain.TypeDef;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class TypescriptTypeWriter implements TypeWriter {
  private final Context ctx;

  @Override
  public void write(List<TypeDef> typeDefs) {
    // TODO 
    typeDefs.forEach(d-> ctx.info("Write type: %s", d));
  }

}

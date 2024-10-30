package org.sharedtype.processor.writer;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.context.OutputTarget;
import org.sharedtype.processor.writer.render.TemplateRenderer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface TypeWriter {
  void write(List<TypeDef> typeDefs) throws IOException;

  static TypeWriter create(Context ctx) {
      Set<TypeWriter> writers = new HashSet<>(OutputTarget.values().length);
      if (ctx.getProps().getTargets().contains(OutputTarget.CONSOLE)) {
          writers.add(new ConsoleWriter(ctx));
      }
      if (ctx.getProps().getTargets().contains(OutputTarget.TYPESCRIPT)) {
          TemplateRenderer renderer = TemplateRenderer.create();
          writers.add(new TypescriptTypeFileWriter(ctx, renderer));
      }
      if (ctx.getProps().getTargets().contains(OutputTarget.JAVA_SERIALIZED)) {
          writers.add(new JavaSerializationFileWriter(ctx));
      }
      return new CompositeWriter(writers);
  }
}

package org.sharedtype.processor.writer;

import java.io.IOException;
import java.util.List;

import org.sharedtype.domain.TypeDef;

public interface TypeWriter {
  void write(List<TypeDef> typeDefs) throws IOException;
}

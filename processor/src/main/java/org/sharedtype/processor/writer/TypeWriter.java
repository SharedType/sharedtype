package org.sharedtype.processor.writer;

import java.util.List;

import org.sharedtype.processor.domain.TypeDef;

public interface TypeWriter {
  void write(List<TypeDef> typeDefs);
}

package org.sharedtype.processor.writer;

import java.util.List;

import org.sharedtype.domain.TypeDef;

public interface TypeWriter {
  void write(List<TypeDef> typeDefs);
}

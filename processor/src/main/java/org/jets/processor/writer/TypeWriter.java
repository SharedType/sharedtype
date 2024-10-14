package org.jets.processor.writer;

import java.util.List;

import org.jets.processor.domain.TypeDef;

public interface TypeWriter {
  void write(List<TypeDef> typeDefs);
}

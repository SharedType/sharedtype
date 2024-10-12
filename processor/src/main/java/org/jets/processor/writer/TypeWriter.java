package org.jets.processor.writer;

import java.util.List;

import org.jets.processor.domain.DefInfo;

public interface TypeWriter {
  void write(List<DefInfo> typeDefs);
}

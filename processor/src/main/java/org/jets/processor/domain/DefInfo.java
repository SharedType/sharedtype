package org.jets.processor.domain;

public interface DefInfo {
  String name();

  /**
   * @return true if all required types are resolved.
   */
  boolean resolved();
}

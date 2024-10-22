package org.sharedtype.processor.domain;

import java.util.List;

public sealed interface TypeDef permits ClassDef, TypeDef.NoneDef {
    String qualifiedName();

    String name();

    List<? extends ComponentInfo> components();

    /**
     * @return true if all required types are resolved.
     */
    boolean resolved();

    static NoneDef none() {
        return NoneDef.INSTANCE;
    }

    final class NoneDef implements TypeDef {
        private static final NoneDef INSTANCE = new NoneDef();
        private NoneDef() {}

        @Override
        public String qualifiedName() {
            return "_NONE_";
        }

        @Override
        public String name() {
            return "_NONE_";
        }

        @Override
        public List<? extends ComponentInfo> components() {
            return List.of();
        }

        @Override
        public boolean resolved() {
            return true;
        }

        @Override
        public String toString() {
            return "_NONE_";
        }
    }
}

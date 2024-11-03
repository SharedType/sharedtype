package org.sharedtype.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import static java.util.Objects.requireNonNull;

final class TypeDefDeserializer {
    static TypeDef deserializeTypeDef(String serFilename) {
        try (InputStream is = TypeDefDeserializer.class.getClassLoader().getResourceAsStream(serFilename);
             ObjectInputStream ois = new ObjectInputStream(requireNonNull(is, "Cannot find " + serFilename))) {
            return (TypeDef) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.sharedtype.domain;

import java.io.IOException;
import java.io.ObjectInputStream;

final class TypeDefDeserializer {
    static TypeDef deserializeTypeDef(String serFilename) {
        try (var is = TypeDefDeserializer.class.getClassLoader().getResourceAsStream(serFilename);
             var ois = new ObjectInputStream(is)) {
            return (TypeDef) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.sharedtype.it;

import org.sharedtype.domain.TypeDef;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import static java.util.Objects.requireNonNull;

final class TypeDefDeserializer {
    private static final ClassLoader classLoader = TypeDefDeserializer.class.getClassLoader();
    private TypeDefDeserializer() {}

    static TypeDef deserializeTypeDef(String serFilename) {
        try (InputStream is = classLoader.getResourceAsStream(serFilename);
             ObjectInputStream ois = new ObjectInputStream(requireNonNull(is, "Cannot find " + serFilename))) {
            return (TypeDef) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean doesResourceExist(String serFilename) {
        return classLoader.getResource(serFilename) != null;
    }
}

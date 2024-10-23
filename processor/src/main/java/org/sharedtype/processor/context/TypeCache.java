package org.sharedtype.processor.context;

import org.sharedtype.processor.domain.TypeDef;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class TypeCache {
    private final Map<String, Container> typeByQualifiedName = new HashMap<>();

    public void saveName(String qualifiedName, String name) {
        typeByQualifiedName.put(qualifiedName, new Container(name, null));
    }
    public void saveType(String qualifiedName, TypeDef typeDef) {
        typeByQualifiedName.put(qualifiedName, new Container(typeDef.name(), typeDef));
    }

    public String getName(String qualifiedName) {
        var container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.simpleName;
    }
    public TypeDef getType(String qualifiedName) {
        var container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.typeDef;
    }

    public boolean contains(String qualifiedName) {
        return typeByQualifiedName.containsKey(qualifiedName);
    }

    private record Container(
        String simpleName,
        @Nullable TypeDef typeDef
    ) {
    }
}

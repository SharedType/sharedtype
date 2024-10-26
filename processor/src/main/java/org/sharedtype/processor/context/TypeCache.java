package org.sharedtype.processor.context;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.domain.TypeInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class TypeCache {
    private final Map<String, Container> typeByQualifiedName = new HashMap<>();

    public void saveName(String qualifiedName, String simpleName) {
        typeByQualifiedName.compute(qualifiedName, (k, v) -> {
            var c = v == null ? new Container() : v;
            c.simpleName = simpleName;
            return c;
        });
    }
    public void saveTypeDef(String qualifiedName, TypeDef typeDef) {
        typeByQualifiedName.compute(qualifiedName, (k, v) -> {
            var c = v == null ? new Container() : v;
            c.simpleName = typeDef.name();
            c.typeDef = typeDef;
            return c;
        });
    }
    public void saveTypeInfo(String qualifiedName, TypeInfo typeInfo) {
        typeByQualifiedName.compute(qualifiedName, (k, v) -> {
            var c = v == null ? new Container() : v;
            c.typeInfo = typeInfo;
            return c;
        });
    }

    public String getName(String qualifiedName) {
        var container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.simpleName;
    }
    public TypeDef getTypeDef(String qualifiedName) {
        var container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.typeDef;
    }
    public TypeInfo getTypeInfo(String qualifiedName) {
        var container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.typeInfo;
    }

    public boolean contains(String qualifiedName) {
        return typeByQualifiedName.containsKey(qualifiedName);
    }

    private static final class Container{
        String simpleName;
        @Nullable TypeDef typeDef;
        @Nullable TypeInfo typeInfo;
    }
}

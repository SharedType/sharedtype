package org.sharedtype.processor.context;

import org.sharedtype.domain.TypeDef;
import org.sharedtype.domain.TypeInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static org.sharedtype.domain.Constants.PREDEFINED_OBJECT_TYPES;

public final class TypeCache {
    private final Map<String, Container> typeByQualifiedName = new HashMap<>();

    TypeCache() {
        PREDEFINED_OBJECT_TYPES.forEach(this::saveTypeInfo);
    }

    public void saveTypeDef(String qualifiedName, TypeDef typeDef) {
        typeByQualifiedName.compute(qualifiedName, (k, v) -> {
            Container c = v == null ? new Container() : v;
            c.typeDef = typeDef;
            return c;
        });
    }
    public void saveTypeInfo(String qualifiedName, TypeInfo typeInfo) {
        typeByQualifiedName.compute(qualifiedName, (k, v) -> {
            Container c = v == null ? new Container() : v;
            c.typeInfo = typeInfo;
            return c;
        });
    }

    public TypeDef getTypeDef(String qualifiedName) {
        Container container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.typeDef;
    }
    public TypeInfo getTypeInfo(String qualifiedName) {
        Container container = typeByQualifiedName.get(qualifiedName);
        return container == null ? null : container.typeInfo;
    }

    public boolean contains(String qualifiedName) {
        return typeByQualifiedName.containsKey(qualifiedName);
    }

    private static final class Container{
        @Nullable TypeDef typeDef;
        @Nullable TypeInfo typeInfo;
    }
}

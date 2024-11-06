package online.sharedtype.processor.context;

import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static online.sharedtype.processor.domain.Constants.PREDEFINED_OBJECT_TYPES;

/**
 * Store and cache type information during annotation processing.
 * <br>
 * A same type can be referenced at multiple places. Once resolved, it should be cached.
 * By design, a type should be represented by only one instance.
 * Note that generic types with different type arguments are different types.
 *
 * @see TypeVariableInfo
 * @see TypeInfoParser
 * @see Context
 * @author Cause Chung
 */
public final class TypeStore {
    private final Map<String, Container> typeByQualifiedName = new HashMap<>();

    TypeStore() {
        PREDEFINED_OBJECT_TYPES.forEach(this::saveTypeInfo);
    }

    public void saveTypeDef(String qualifiedName, TypeDef typeDef) {
        typeByQualifiedName.compute(qualifiedName, (k, v) -> {
            Container c = v == null ? new Container() : v;
            c.typeDef = typeDef;
            return c;
        });
    }
    /**
     * Can only cache non-generic type.
     */
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

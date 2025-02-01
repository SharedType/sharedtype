package online.sharedtype.processor.context;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static online.sharedtype.processor.domain.Constants.PREDEFINED_OBJECT_TYPES;

/**
 * Store and cache type information during annotation processing.
 * <br>
 * A same type can be referenced at multiple places. Once resolved, it should be cached.
 *
 * @see TypeVariableInfo
 * @see TypeInfoParser
 * @see Context
 * @author Cause Chung
 */
public final class TypeStore {
    private final Map<String, TypeDef> typeDefByQualifiedName = new HashMap<>();
    private final Map<TypeInfoKey, TypeInfo> typeInfoByKey = new HashMap<>();
    private final Map<TypeDef, Config> typeConfig = new HashMap<>();

    TypeStore() {
        for (Map.Entry<String, ConcreteTypeInfo> entry : PREDEFINED_OBJECT_TYPES.entrySet()) {
            saveTypeInfo(entry.getKey(), entry.getValue().typeArgs(), entry.getValue());
        }
    }

    public void saveTypeDef(String qualifiedName, TypeDef typeDef) {
        typeDefByQualifiedName.put(qualifiedName, typeDef);
    }

    public void saveTypeInfo(String qualifiedName, List<? extends TypeInfo> typeArgs, TypeInfo typeInfo) {
        typeInfoByKey.put(new TypeInfoKey(qualifiedName, typeArgs), typeInfo);
    }

    public TypeDef getTypeDef(String qualifiedName) {
        return typeDefByQualifiedName.get(qualifiedName);
    }
    public TypeInfo getTypeInfo(String qualifiedName, List<? extends TypeInfo> typeArgs) {
        return typeInfoByKey.get(new TypeInfoKey(qualifiedName, typeArgs));
    }

    public boolean containsTypeDef(String qualifiedName) {
        return typeDefByQualifiedName.containsKey(qualifiedName);
    }

    public void saveConfig(TypeDef typeDef, Config config) {
        typeConfig.put(typeDef, config);
    }
    @Nullable
    public Config getConfig(TypeDef typeDef) {
        return typeConfig.get(typeDef);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static final class TypeInfoKey {
        final String qualifiedName;
        final List<? extends TypeInfo> typeArgs;
    }
}

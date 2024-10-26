package org.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class TypescriptTypeFileWriter implements TypeWriter {
    private static final Map<String, String> TYPE_NAME_MAPPINGS = Map.ofEntries(
        Map.entry("boolean", "boolean"),
        Map.entry("byte", "number"),
        Map.entry("char", "string"),
        Map.entry("double", "number"),
        Map.entry("float", "number"),
        Map.entry("int", "number"),
        Map.entry("long", "number"),
        Map.entry("short", "number"),
        Map.entry("java.lang.Boolean", "boolean"),
        Map.entry("java.lang.Byte", "number"),
        Map.entry("java.lang.Character", "string"),
        Map.entry("java.lang.Double", "number"),
        Map.entry("java.lang.Float", "number"),
        Map.entry("java.lang.Integer", "number"),
        Map.entry("java.lang.Long", "number"),
        Map.entry("java.lang.Short", "number"),
        Map.entry("java.lang.String", "string"),
        Map.entry("java.lang.Void", "never")
    );

    private final Context ctx;

    @Override
    public void write(List<TypeDef> typeDefs) {
        // TODO
    }

}

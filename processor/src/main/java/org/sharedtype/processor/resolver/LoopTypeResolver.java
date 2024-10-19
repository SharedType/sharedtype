package org.sharedtype.processor.resolver;

import lombok.RequiredArgsConstructor;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.*;
import org.sharedtype.processor.parser.TypeDefParser;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class LoopTypeResolver implements TypeResolver {
    private static final int DEPENDENCY_COUNT_EXPANSION_FACTOR = 2; // TODO: find a proper number
    private final Context ctx;
    private final TypeDefParser typeDefParser;

    @Override
    public List<TypeDef> resolve(List<TypeDef> typeDefs) {
        var resolvedDefs = new ArrayList<TypeDef>(typeDefs.size() * DEPENDENCY_COUNT_EXPANSION_FACTOR);
        var processingDefs = new ArrayDeque<>(typeDefs);

        while (!processingDefs.isEmpty()) {
            var defInfo = processingDefs.pop();
            if (defInfo.resolved()) {
                resolvedDefs.add(defInfo);
                continue;
            }

            processingDefs.push(defInfo);

            if (defInfo instanceof ClassDef classInfo) {
                for (FieldInfo fieldInfo : classInfo.components()) {
                    if (!fieldInfo.resolved()) {
                        var dependentDefs = tryRecursivelyResolve(fieldInfo.type());
                        dependentDefs.forEach(processingDefs::push);
                    }
                }
            } else {
                throw new SharedTypeInternalError("Unsupported type: " + defInfo.getClass());
            }
        }

        return resolvedDefs;
    }

    private List<TypeDef> tryRecursivelyResolve(TypeInfo typeInfo) {
        if (typeInfo instanceof ConcreteTypeInfo concreteTypeInfo) {
            var defs = new ArrayList<TypeDef>();
            if (!concreteTypeInfo.shallowResolved()) {
                var typeElement = ctx.getProcessingEnv().getElementUtils().getTypeElement(concreteTypeInfo.qualifiedName());
                var parsedList = typeDefParser.parse(typeElement);
                concreteTypeInfo.setSimpleName(parsedList.get(0).name());
                concreteTypeInfo.markShallowResolved();
                defs.addAll(parsedList);
            }
            for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                defs.addAll(tryRecursivelyResolve(typeArg));
            }
            return defs;
        }
        throw new SharedTypeInternalError(String.format("Only ConcreteTypeInfo needs to be resolved, but got: %s with class %s", typeInfo, typeInfo.getClass()));
    }
}

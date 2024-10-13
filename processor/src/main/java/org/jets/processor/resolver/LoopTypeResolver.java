package org.jets.processor.resolver;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.Context;
import org.jets.processor.domain.ClassInfo;
import org.jets.processor.domain.DefInfo;
import org.jets.processor.domain.FieldInfo;
import org.jets.processor.domain.TypeInfo;
import org.jets.processor.parser.TypeElementParser;
import org.jets.processor.support.exception.JetsInternalError;

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
    private final TypeElementParser typeElementParser;

    @Override
    public List<DefInfo> resolve(List<DefInfo> typeDefs) {
        var resolvedDefs = new ArrayList<DefInfo>(typeDefs.size() * DEPENDENCY_COUNT_EXPANSION_FACTOR);
        var processingDefs = new ArrayDeque<>(typeDefs);

        while (!processingDefs.isEmpty()) {
            var defInfo = processingDefs.pop();
            if (defInfo.resolved()) {
                resolvedDefs.add(defInfo);
                continue;
            }

            processingDefs.push(defInfo);

            if (defInfo instanceof ClassInfo classInfo) {
                for (FieldInfo fieldInfo : classInfo.components()) {
                    if (!fieldInfo.resolved()) {
                        var dependentDefs = tryRecursivelyResolve(fieldInfo.typeInfo());
                        dependentDefs.forEach(processingDefs::push);
                    }
                }
            } else {
                throw new JetsInternalError("Unsupported type: " + defInfo.getClass());
            }
        }

        return resolvedDefs;
    }

    private List<DefInfo> tryRecursivelyResolve(TypeInfo typeInfo) {
        var defs = new ArrayList<DefInfo>();
        if (!typeInfo.shallowResolved()) {
            var typeElement = ctx.getProcessingEnv().getElementUtils().getTypeElement(typeInfo.qualifiedName());
            var parsedList = typeElementParser.parse(typeElement);
            typeInfo.setSimpleName(parsedList.get(0).name());
            typeInfo.markShallowResolved();
            defs.addAll(parsedList);
        }
        for (TypeInfo typeArg : typeInfo.typeArgs()) {
            defs.addAll(tryRecursivelyResolve(typeArg));
        }
        return defs;
    }
}

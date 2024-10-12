package org.jets.processor.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jets.processor.context.Context;
import org.jets.processor.domain.ClassInfo;
import org.jets.processor.domain.DefInfo;
import org.jets.processor.domain.FieldInfo;
import org.jets.processor.parser.TypeParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class LoopTypeResolver implements TypeResolver {
    private static final int DEPENDENCY_COUNT_EXPANSION_FACTOR = 2; // TODO: find a proper number
    private final Context ctx;
    private final TypeParser typeParser;

    @Override
    public List<DefInfo> resolve(List<DefInfo> typeDefs) {
        var resolvedDefs = new ArrayList<DefInfo>(typeDefs.size() * DEPENDENCY_COUNT_EXPANSION_FACTOR);
        var unresolvedDefs = new HashSet<DefInfo>(typeDefs);

        while (!unresolvedDefs.isEmpty()) {
            for (DefInfo defInfo : typeDefs) {
                if (defInfo.resolved()) {
                    resolvedDefs.add(defInfo);
                    unresolvedDefs.remove(defInfo);
                    continue;
                }

                if (defInfo instanceof ClassInfo classInfo) {
                    for (FieldInfo fieldInfo : classInfo.fields()) {
                        if (!fieldInfo.typeResolved()) {
                            var resolvedWithDependents = tryResolveOne(fieldInfo.javaQualifiedTypename());
                            for (DefInfo resolved : resolvedWithDependents) {

                            }
                        }
                    }
                }

            }
        }

        return resolvedDefs;
    }

    private List<DefInfo> tryResolveOne(String qualifiedTypename) {
        var typeElement = ctx.getProcessingEnv().getElementUtils().getTypeElement(qualifiedTypename);
        // TODO: validation

        return typeParser.parse(typeElement);
    }

}

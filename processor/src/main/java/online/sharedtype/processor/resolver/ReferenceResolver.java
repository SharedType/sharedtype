package online.sharedtype.processor.resolver;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.annotation.SideEffect;
import online.sharedtype.processor.support.exception.SharedTypeException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traverse type graph to mark:
 * <ul>
 *     <li>cyclic references</li>
 *     <li>direct or indirect reference from explicitly annotated classes</li>
 * </ul>
 */
@RequiredArgsConstructor
final class ReferenceResolver implements TypeResolver {
    private final Context ctx;

    @Override
    public List<TypeDef> resolve(@SideEffect List<TypeDef> typeDefs) {
        for (TypeDef typeDef : typeDefs) {
            traverse(typeDef);
        }
        return typeDefs;
    }

    private void traverse(TypeDef typeDef) {
        Set<TypeDef> visited = new HashSet<>();
        Deque<TypeDef> typeDefStack = new ArrayDeque<>();
        typeDefStack.push(typeDef);
        boolean referencedByAnnotated = typeDef.isReferencedByAnnotated();

        while (!typeDefStack.isEmpty()) {
            TypeDef cur = typeDefStack.pop();
            if (visited.contains(cur)) {
                cur.setCyclicReferenced(true);
                if (ctx.getProps().getCyclicReferenceReportStrategy() == Props.CyclicReferenceReportStrategy.ERROR) {
                    throw new SharedTypeException(String.format("Type '%s' is cyclically referenced, which is not supported." +
                        " You can change the configuration to warn and ignore cyclically referenced types.", cur.qualifiedName()));
                } else {
                    ctx.warn("Type '%s' is cyclically referenced, which will not be emitted. Cyclic references are not supported.", cur.qualifiedName());
                }
                continue;
            } else {
                visited.add(cur);
            }

            if (cur instanceof ClassDef) {
                ClassDef curClassDef = (ClassDef) cur;
                List<TypeDef> referencingTypeDefs = curClassDef.typeInfoSet().stream()
                    .flatMap(ts -> ts.referencingTypes().stream()).collect(Collectors.toList());
                for (TypeDef referencingTypeDef : referencingTypeDefs) {
                    if (referencingTypeDef != null) {
                        typeDefStack.push(referencingTypeDef);
                    }
                    if (referencingTypeDef instanceof ClassDef) {
                        ClassDef dependingClassDef = (ClassDef) referencingTypeDef;
                        if (dependingClassDef.isAnnotated() || dependingClassDef.isReferencedByAnnotated()) {
                            cur.setReferencedByAnnotated(true);
                            referencedByAnnotated = true;
                        }
                    }
                }
            }
        }
        typeDef.setReferencedByAnnotated(referencedByAnnotated);
    }
}

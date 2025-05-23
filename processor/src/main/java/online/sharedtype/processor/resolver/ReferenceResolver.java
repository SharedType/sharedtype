package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.def.ConcreteTypeDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.annotation.SideEffect;

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
final class ReferenceResolver implements TypeResolver {

    @Override
    public List<TypeDef> resolve(@SideEffect List<TypeDef> typeDefs) {
        for (TypeDef typeDef : typeDefs) {
            traverse(typeDef);
        }
        return typeDefs;
    }

    private static void traverse(TypeDef typeDef) {
        Set<TypeDef> visited = new HashSet<>();
        Deque<TypeDef> typeDefStack = new ArrayDeque<>();
        typeDefStack.push(typeDef);
        boolean referencedByAnnotated = typeDef.isReferencedByAnnotated();

        while (!typeDefStack.isEmpty()) {
            TypeDef cur = typeDefStack.pop();
            if (visited.contains(cur)) {
                cur.setCyclicReferenced(true);
                continue;
            } else {
                visited.add(cur);
            }

            if (cur instanceof ConcreteTypeDef) {
                ConcreteTypeDef curConcreteTypeDef = (ConcreteTypeDef) cur;
                List<TypeDef> referencingTypeDefs = curConcreteTypeDef.typeInfoSet().stream()
                    .flatMap(ts -> ts.referencingTypes().stream()).collect(Collectors.toList());
                if (!referencingTypeDefs.isEmpty()){
                    curConcreteTypeDef.setDepended(true);
                }
                for (TypeDef referencingTypeDef : referencingTypeDefs) {
                    typeDefStack.push(referencingTypeDef);
                    if (referencingTypeDef instanceof ConcreteTypeDef) {
                        ConcreteTypeDef dependingClassDef = (ConcreteTypeDef) referencingTypeDef;
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

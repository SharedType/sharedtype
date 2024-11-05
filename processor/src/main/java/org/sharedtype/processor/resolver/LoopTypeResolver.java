package org.sharedtype.processor.resolver;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.ArrayTypeInfo;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.FieldComponentInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.domain.TypeInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.TypeDefParser;
import org.sharedtype.support.annotation.SideEffect;
import org.sharedtype.support.exception.SharedTypeInternalError;

import javax.lang.model.element.TypeElement;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Implementation that uses stacks to traverse the type graph.
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class LoopTypeResolver implements TypeResolver {
    private static final int DEPENDENCY_COUNT_EXPANSION_FACTOR = 2; // TODO: find a proper number
    private final Context ctx;
    private final TypeDefParser typeDefParser;

    @Override
    public List<TypeDef> resolve(List<TypeDef> typeDefs) {
        int n = typeDefs.size() * DEPENDENCY_COUNT_EXPANSION_FACTOR;
        LinkedHashSet<TypeDef> resolvedDefs = new LinkedHashSet<>(n);
        Deque<TypeDef> processingDefStack = new ArrayDeque<>(n); // TODO: pass metadata from ctx to better size these buffers
        Deque<TypeInfo> processingInfoStack = new ArrayDeque<>(n);
        processingDefStack.addAll(typeDefs);

        while (!processingDefStack.isEmpty()) {
            TypeDef typeDef = processingDefStack.pop();
            if (resolvedDefs.contains(typeDef)) {
                continue;
            }
            if (typeDef.resolved()) {
                resolvedDefs.add(typeDef);
                continue;
            }

            processingDefStack.push(typeDef);

            if (typeDef instanceof ClassDef) {
                ClassDef classDef = (ClassDef) typeDef;
                for (FieldComponentInfo fieldComponentInfo : classDef.components()) {
                    if (!fieldComponentInfo.resolved()) {
                        processingInfoStack.push(fieldComponentInfo.type());
                    }
                }
                for (TypeInfo supertype : classDef.supertypes()) {
                    if (!supertype.resolved()) {
                        processingInfoStack.push(supertype);
                    }
                }
                for (TypeVariableInfo typeVariableInfo : classDef.typeVariables()) {
                    if (!typeVariableInfo.resolved()) {
                        processingInfoStack.push(typeVariableInfo);
                    }
                }
            } else if (typeDef instanceof EnumDef) {
                EnumDef enumDef = (EnumDef) typeDef;
                for (EnumValueInfo component : enumDef.components()) {
                    if (!component.resolved()) {
                        processingInfoStack.push(component.type());
                    }
                }
            } else {
                throw new SharedTypeInternalError("Unsupported type: " + typeDef.getClass());
            }

            resolveTypeInfo(processingDefStack, processingInfoStack);
        }

        return new ArrayList<>(resolvedDefs);
    }

    @SideEffect
    private void resolveTypeInfo(Deque<TypeDef> processingDefStack, Deque<TypeInfo> processingInfoStack) {
        while (!processingInfoStack.isEmpty()) {
            TypeInfo typeInfo = processingInfoStack.pop();
            if (typeInfo instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
                if (!concreteTypeInfo.shallowResolved()) {
                    TypeElement typeElement = ctx.getProcessingEnv().getElementUtils().getTypeElement(concreteTypeInfo.qualifiedName());
                    TypeDef parsed = typeDefParser.parse(typeElement);
                    if (parsed != null) {
                        concreteTypeInfo.markShallowResolved();
                        processingDefStack.push(parsed);
                    }
                }
                for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                    if (!typeArg.resolved()) {
                        processingInfoStack.push(typeArg);
                    }
                }
            } else if (typeInfo instanceof ArrayTypeInfo) {
                ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
                if (!arrayTypeInfo.resolved()) {
                    processingInfoStack.push(arrayTypeInfo.component());
                }
            } else if (typeInfo instanceof TypeVariableInfo) {
                TypeVariableInfo typeVariableInfo = (TypeVariableInfo) typeInfo;
                throw new UnsupportedOperationException("Type variable not supported yet: " + typeVariableInfo);
            } else {
                throw new SharedTypeInternalError(
                    String.format("Only ConcreteTypeInfo needs to be resolved, but got typeInfo: %s with %s", typeInfo, typeInfo.getClass())
                );
            }
        }
    }
}

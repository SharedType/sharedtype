package online.sharedtype.processor.resolver;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.parser.TypeDefParser;
import online.sharedtype.processor.support.annotation.SideEffect;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

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
    private static final int MAX_LOOP = 2000;
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

        int loopCount = 0;
        while (!processingDefStack.isEmpty()) {
            loopCount++;
            if (loopCount > MAX_LOOP) {
                throw new SharedTypeInternalError("Resolving typeDefs has exceeded the maximum loop count.");
            }
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
                for (TypeInfo supertype : classDef.directSupertypes()) {
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
            } else if (typeDef instanceof ConstantNamespaceDef) {
                ConstantNamespaceDef constantNamespaceDef = (ConstantNamespaceDef) typeDef;
                for (ConstantField constantField : constantNamespaceDef.components()) {
                    if (!constantField.resolved()) {
                        processingInfoStack.push(constantField.type());
                    }
                }
            } else {
                throw new SharedTypeInternalError(String.format("Unsupported TypeDef:'%s' of type '%s'", typeDef.qualifiedName(), typeDef.getClass()));
            }

            resolveTypeInfo(processingDefStack, processingInfoStack);
        }

        return new ArrayList<>(resolvedDefs);
    }

    @SideEffect
    private void resolveTypeInfo(Deque<TypeDef> processingDefStack, Deque<TypeInfo> processingInfoStack) {
        int loopCount = 0;
        while (!processingInfoStack.isEmpty()) {
            loopCount++;
            if (loopCount > MAX_LOOP) {
                throw new SharedTypeInternalError("Resolving typeInfo has exceeded the maximum loop count.");
            }
            TypeInfo typeInfo = processingInfoStack.pop();
            if (typeInfo instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
                if (!concreteTypeInfo.shallowResolved()) {
                    TypeElement typeElement = ctx.getProcessingEnv().getElementUtils().getTypeElement(concreteTypeInfo.qualifiedName());
                    List<TypeDef> parsed = typeDefParser.parse(typeElement);
                    if (!parsed.isEmpty()) {
                        TypeDef mainTypeDef = parsed.get(0);
                        concreteTypeInfo.markShallowResolved(mainTypeDef);
                        processingDefStack.push(mainTypeDef);
                    } else if (ctx.isIgnored(typeElement)) {
                        concreteTypeInfo.markShallowResolved(null);
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
                throw new SharedTypeInternalError("TypeVariableInfo is not supported yet: " + typeVariableInfo);
            } else {
                throw new SharedTypeInternalError(String.format(
                    "Only ConcreteTypeInfo needs to be resolved, but got: %s of %s", typeInfo, typeInfo.getClass()));
            }
        }
    }
}

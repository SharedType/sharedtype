package online.sharedtype.processor.resolver;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.component.FieldComponentInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.MapTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.support.annotation.SideEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Field outermost optional type will be converted to optional marker, nested optional types will be flattened.
 *
 * @see online.sharedtype.SharedType
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class OptionalTypeResolver implements TypeResolver {
    private final Context ctx;

    /**
     * The input typeDefs themselves should not be any optional types, because optional typeInfo should be marked as resolved at parsing.
     * @param typeDefs the types fully resolved by {@link LoopTypeResolver}.
     * @return typeDefs with fields containing optional types converted or flattened.
     * @see online.sharedtype.processor.parser.type.TypeInfoParser
     */
    @Override
    public List<TypeDef> resolve(List<TypeDef> typeDefs) {
        List<TypeDef> result = new ArrayList<>(typeDefs.size());
        for (TypeDef typeDef : typeDefs) {
            if (typeDef instanceof ClassDef) {
                ClassDef classDef = (ClassDef) typeDef;
                resolveOptionalTypes(classDef);
            }
            result.add(typeDef);
        }
        return result;
    }

    private void resolveOptionalTypes(@SideEffect ClassDef classDef) {
        for (FieldComponentInfo component : classDef.components()) {
            TypeInfo fieldTypeInfo = component.type();
            if (fieldTypeInfo instanceof ConcreteTypeInfo) {
                ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) fieldTypeInfo;
                if (ctx.isOptionalType(concreteTypeInfo.qualifiedName())) { // outermost optional type
                    component.setOptional(true);
                }
            }
            component.setType(recursivelyFlattenOptionalTypes(fieldTypeInfo, classDef));
        }
    }

    private TypeInfo recursivelyFlattenOptionalTypes(@SideEffect TypeInfo typeInfo, ClassDef ctxTypeDef) {
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            if (ctx.isOptionalType(concreteTypeInfo.qualifiedName())) {
                if (concreteTypeInfo.typeArgs().size() != 1) {
                    ctx.error(ctxTypeDef.getElement(),
                        "Optional type %s in %s should have exactly one type argument. Check configuration if optional types are wrongly defined.",
                        concreteTypeInfo.qualifiedName(), ctxTypeDef.qualifiedName());
                }
                return recursivelyFlattenOptionalTypes(concreteTypeInfo.typeArgs().get(0), ctxTypeDef);
            } else {
                if (!concreteTypeInfo.typeArgs().isEmpty()) {
                    concreteTypeInfo.typeArgs().replaceAll(elem -> recursivelyFlattenOptionalTypes(elem, ctxTypeDef));
                }
            }
        } else if (typeInfo instanceof ArrayTypeInfo) {
            ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
            return new ArrayTypeInfo(recursivelyFlattenOptionalTypes(arrayTypeInfo.component(), ctxTypeDef));
        } else if (typeInfo instanceof MapTypeInfo) {
            MapTypeInfo mapTypeInfo = (MapTypeInfo) typeInfo;
            return mapTypeInfo.toBuilder().valueType(recursivelyFlattenOptionalTypes(mapTypeInfo.valueType(), ctxTypeDef)).build();
        }
        return typeInfo;
    }
}

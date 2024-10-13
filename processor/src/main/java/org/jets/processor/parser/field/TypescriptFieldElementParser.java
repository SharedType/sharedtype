package org.jets.processor.parser.field;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.jets.processor.context.Context;
import org.jets.processor.domain.TypeInfo;

@Singleton
final class TypescriptFieldElementParser implements FieldElementParser {
    private static final Map<TypeKind, TypeInfo> PRIMITIVES = Map.of(
        TypeKind.BOOLEAN, TypeInfo.ofPredefined("boolean", "boolean"),
        TypeKind.BYTE, TypeInfo.ofPredefined("number", "number"),
        TypeKind.CHAR, TypeInfo.ofPredefined("string", "string"),
        TypeKind.DOUBLE, TypeInfo.ofPredefined("number", "number"),
        TypeKind.FLOAT, TypeInfo.ofPredefined("number", "number"),
        TypeKind.INT, TypeInfo.ofPredefined("number", "number"),
        TypeKind.LONG, TypeInfo.ofPredefined("number", "number"),
        TypeKind.SHORT, TypeInfo.ofPredefined("number", "number")
    );
    private static final Map<String, TypeInfo> PREDEFINED_OBJECT_TYPES = Map.of(
        "java.lang.Boolean", TypeInfo.ofPredefined("java.lang.Boolean", "boolean"),
        "java.lang.Byte", TypeInfo.ofPredefined("java.lang.Byte", "number"),
        "java.lang.Character", TypeInfo.ofPredefined("java.lang.Character", "string"),
        "java.lang.Double", TypeInfo.ofPredefined("java.lang.Double", "number"),
        "java.lang.Float", TypeInfo.ofPredefined("java.lang.Float", "number"),
        "java.lang.Integer", TypeInfo.ofPredefined("java.lang.Integer", "number"),
        "java.lang.Long", TypeInfo.ofPredefined("java.lang.Long", "number"),
        "java.lang.Short", TypeInfo.ofPredefined("java.lang.Short", "number"),
        "java.lang.String", TypeInfo.ofPredefined("java.lang.String", "string"),
        "java.lang.Void", TypeInfo.ofPredefined("java.lang.Void", "never")
    );

    private static final String OBJECT_NAME = Object.class.getName();
    private final Context ctx;
    private final Types types;
    private final Elements elements;
    private final Map<String, TypeInfo> predefinedObjectTypes;

    @Inject
    TypescriptFieldElementParser(Context ctx) {
        this.ctx = ctx;
        this.types = ctx.getProcessingEnv().getTypeUtils();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        this.predefinedObjectTypes = new HashMap<>(PREDEFINED_OBJECT_TYPES);
        predefinedObjectTypes.put(OBJECT_NAME, TypeInfo.ofPredefined(OBJECT_NAME, ctx.getProps().getJavaObjectMapType()));
    }

    @Override
    public TypeInfo parse(VariableElement element) {
        var typeMirror = element.asType();
        var typeKind = typeMirror.getKind();

        if (typeKind.isPrimitive()) {
            return PRIMITIVES.get(typeKind);
        } else if (typeKind == TypeKind.ARRAY) {
            throw new UnsupportedOperationException("Not implemented:" + typeMirror);
        } else if (typeKind == TypeKind.DECLARED) {
            return parseDeclared((DeclaredType) typeMirror);
        }
        ctx.error("Unsupported element: %s, kind: %s", element, typeKind);
        return null;
    }

    // TODO: type arguments
    private TypeInfo parseDeclared(DeclaredType declaredType) {
        var typeElement = (TypeElement)declaredType.asElement();
        var qualifiedName = typeElement.getQualifiedName().toString();
        var predefinedTypeInfo = predefinedObjectTypes.get(qualifiedName);
        if (predefinedTypeInfo != null) {
            return predefinedTypeInfo;
        }

        // TODO: check for collection and go for array

        if (ctx.hasType(qualifiedName)) {
            return TypeInfo.builder()
                    .qualifiedName(qualifiedName)
                    .simpleName(ctx.getSimpleName(qualifiedName))
                    .build();
        }
        return TypeInfo.builder()
                .qualifiedName(qualifiedName)
                .resolved(false)
                .build();
    }
}

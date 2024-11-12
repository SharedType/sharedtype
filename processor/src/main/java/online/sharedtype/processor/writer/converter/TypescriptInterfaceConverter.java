package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class TypescriptInterfaceConverter implements TemplateDataConverter {
    private static final Map<ConcreteTypeInfo, String> PREDEFINED_TYPE_NAME_MAPPINGS;
    static {
        Map<ConcreteTypeInfo, String> tempMap = new HashMap<>(20);
        tempMap.put(Constants.BOOLEAN_TYPE_INFO, "boolean");
        tempMap.put(Constants.BYTE_TYPE_INFO, "number");
        tempMap.put(Constants.CHAR_TYPE_INFO, "string");
        tempMap.put(Constants.DOUBLE_TYPE_INFO, "number");
        tempMap.put(Constants.FLOAT_TYPE_INFO, "number");
        tempMap.put(Constants.INT_TYPE_INFO, "number");
        tempMap.put(Constants.LONG_TYPE_INFO, "number");
        tempMap.put(Constants.SHORT_TYPE_INFO, "number");

        tempMap.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "boolean");
        tempMap.put(Constants.BOXED_BYTE_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_CHAR_TYPE_INFO, "string");
        tempMap.put(Constants.BOXED_DOUBLE_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_FLOAT_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_INT_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_LONG_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_SHORT_TYPE_INFO, "number");

        tempMap.put(Constants.STRING_TYPE_INFO, "string");
        tempMap.put(Constants.VOID_TYPE_INFO, "never");

        PREDEFINED_TYPE_NAME_MAPPINGS = Collections.unmodifiableMap(tempMap);
    }

    private final Context ctx;
    private final Map<ConcreteTypeInfo, String> typeNameMappings;
    private final char interfacePropertyDelimiter;

    TypescriptInterfaceConverter(Context ctx) {
        this.ctx = ctx;
        interfacePropertyDelimiter = ctx.getProps().getTypescript().getInterfacePropertyDelimiter();
        typeNameMappings = new HashMap<>(PREDEFINED_TYPE_NAME_MAPPINGS);
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getTypescript().getJavaObjectMapType());
    }

    @Override
    public boolean supports(TypeDef typeDef) {
        return typeDef instanceof ClassDef;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        InterfaceExpr value = new InterfaceExpr(
            classDef.simpleName(),
            classDef.typeVariables().stream().map(this::toTypeExpr).collect(Collectors.toList()),
            classDef.supertypes().stream().map(this::toTypeExpr).collect(Collectors.toList()),
            classDef.components().stream().map(this::toPropertyExpr).collect(Collectors.toList())
        );
        return Tuple.of(Template.TEMPLATE_TYPESCRIPT_INTERFACE, value);
    }


    private PropertyExpr toPropertyExpr(FieldComponentInfo field) {
        return new PropertyExpr(
            field.name(),
            toTypeExpr(field.type()),
            interfacePropertyDelimiter,
            field.optional(),
            false,
            false // TODO: more options
        );
    }

    private String toTypeExpr(TypeInfo typeInfo) {
        StringBuilder typeExprBuilder = new StringBuilder();
        buildTypeExprRecursively(typeInfo, typeExprBuilder);
        return typeExprBuilder.toString();
    }

    private void buildTypeExprRecursively(TypeInfo typeInfo, @online.sharedtype.processor.support.annotation.SideEffect StringBuilder nameBuilder) { // TODO: abstract up
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            nameBuilder.append(typeNameMappings.getOrDefault(concreteTypeInfo, concreteTypeInfo.simpleName()));
            if (!concreteTypeInfo.typeArgs().isEmpty()) {
                nameBuilder.append("<");
                for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                    buildTypeExprRecursively(typeArg, nameBuilder);
                    nameBuilder.append(", ");
                }
                nameBuilder.setLength(nameBuilder.length() - 2);
                nameBuilder.append(">");
            }
        } else if (typeInfo instanceof TypeVariableInfo) {
            TypeVariableInfo typeVariableInfo = (TypeVariableInfo) typeInfo;
            nameBuilder.append(typeVariableInfo.name());
        } else if (typeInfo instanceof ArrayTypeInfo) {
            ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
            buildTypeExprRecursively(arrayTypeInfo.component(), nameBuilder);
            nameBuilder.append("[]");
        }
    }

    @RequiredArgsConstructor
    @SuppressWarnings("unused")
    static final class InterfaceExpr {
        final String name;
        final List<String> typeParameters;
        final List<String> supertypes;
        final List<PropertyExpr> properties;

        String typeParametersExpr() {
            if (typeParameters.isEmpty()) {
                return null;
            }
            return String.format("<%s>", String.join(", ", typeParameters));
        }

        String supertypesExpr() {
            if (supertypes.isEmpty()) {
                return null;
            }
            return String.format("extends %s ", String.join(", ", supertypes));
        }
    }

    @RequiredArgsConstructor
    @SuppressWarnings("unused")
    static final class PropertyExpr{
        final String name;
        final String type;
        final char propDelimiter;
        final boolean optional;
        final boolean unionNull;
        final boolean unionUndefined;
    }
}

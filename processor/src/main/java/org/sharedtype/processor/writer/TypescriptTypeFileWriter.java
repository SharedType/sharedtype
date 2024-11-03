package org.sharedtype.processor.writer;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.ArrayTypeInfo;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.Constants;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.FieldComponentInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.domain.TypeInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.support.annotation.SideEffect;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;
import org.sharedtype.processor.support.utils.Tuple;
import org.sharedtype.processor.writer.render.Template;
import org.sharedtype.processor.writer.render.TemplateRenderer;

import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class TypescriptTypeFileWriter implements TypeWriter {
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
    private final Elements elements;
    private final Map<ConcreteTypeInfo, String> typeNameMappings;
    private final TemplateRenderer renderer;
    private final char interfacePropertyDelimiter;

    TypescriptTypeFileWriter(Context ctx, TemplateRenderer renderer) {
        this.ctx = ctx;
        elements = ctx.getProcessingEnv().getElementUtils();
        this.renderer = renderer;
        interfacePropertyDelimiter = ctx.getProps().getTypescript().getInterfacePropertyDelimiter();

        typeNameMappings = new HashMap<>(PREDEFINED_TYPE_NAME_MAPPINGS);
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getTypescript().getJavaObjectMapType());
        renderer.loadTemplates(
            Template.TEMPLATE_INTERFACE,
            Template.TEMPLATE_ENUM_UNION
        );
    }

    @Override
    public void write(List<TypeDef> typeDefs) throws IOException {
        List<Tuple<Template, Object>> data = new ArrayList<>(typeDefs.size());
        Map<String, TypeDef> simpleNames = new HashMap<>(typeDefs.size());
        for (TypeDef typeDef : typeDefs) {
            TypeDef duplicate = simpleNames.get(typeDef.simpleName());
            if (duplicate != null) {
                ctx.error("Duplicate names found: %s and %s, which is not allowed in output typescript code." +
                    " You may use @SharedType(name=\"...\") to rename a type.", typeDef.qualifiedName(), duplicate.qualifiedName());
                return;
            }
            simpleNames.put(typeDef.simpleName(), typeDef);
            if (typeDef instanceof EnumDef) {
                EnumDef enumDef = (EnumDef) typeDef;
                List<String> values = new ArrayList<>(enumDef.components().size());
                for (EnumValueInfo component : enumDef.components()) {
                    try {
                        String result = elements.getConstantExpression(component.value());
                        values.add(result);
                    } catch (IllegalArgumentException e) {
                        throw new SharedTypeInternalError(String.format(
                            "Failed to get constant expression for enum value: %s of type %s in enum: %s", component.value(), component.type(), enumDef), e);
                    }
                }
                data.add(Tuple.of(Template.TEMPLATE_ENUM_UNION, new EnumUnionExpr(enumDef.simpleName(), values)));
            } else if (typeDef instanceof ClassDef) {
                ClassDef classDef = (ClassDef) typeDef;
                InterfaceExpr value = new InterfaceExpr(
                    classDef.simpleName(),
                    classDef.typeVariables().stream().map(this::toTypeExpr).collect(Collectors.toList()),
                    classDef.supertypes().stream().map(this::toTypeExpr).collect(Collectors.toList()),
                    classDef.components().stream().map(this::toPropertyExpr).collect(Collectors.toList())
                );
                data.add(Tuple.of(Template.TEMPLATE_INTERFACE, value));
            }
        }

        FileObject file = ctx.createSourceOutput(ctx.getProps().getTypescript().getOutputFileName()); // TODO: abstract up
        try (OutputStream outputStream = file.openOutputStream();
             Writer writer = new OutputStreamWriter(outputStream)) {
            renderer.render(writer, data);
        }
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

    private void buildTypeExprRecursively(TypeInfo typeInfo, @SideEffect StringBuilder nameBuilder) { // TODO: abstract up
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
            nameBuilder.append(typeVariableInfo.getName());
        } else if (typeInfo instanceof ArrayTypeInfo) {
            ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
            buildTypeExprRecursively(arrayTypeInfo.component(), nameBuilder);
            nameBuilder.append("[]");
        }
    }

    @RequiredArgsConstructor
    @SuppressWarnings("unused")
    static final class InterfaceExpr{
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
    static final class PropertyExpr{
        final String name;
        final String type;
        final char propDelimiter;
        final boolean optional;
        final boolean unionNull;
        final boolean unionUndefined;
    }

    @RequiredArgsConstructor
    static final class EnumUnionExpr {
        final String name;
        final List<String> values;

        @SuppressWarnings("unused")
        String valuesExpr() {
            return String.join(" | ", values);
        }
    }
}

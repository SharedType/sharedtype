package org.sharedtype.processor.writer;

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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TypescriptTypeFileWriter implements TypeWriter {
    private static final Map<ConcreteTypeInfo, String> PREDEFINED_TYPE_NAME_MAPPINGS = Map.ofEntries(
        Map.entry(Constants.BOOLEAN_TYPE_INFO, "boolean"),
        Map.entry(Constants.BYTE_TYPE_INFO, "number"),
        Map.entry(Constants.CHAR_TYPE_INFO, "string"),
        Map.entry(Constants.DOUBLE_TYPE_INFO, "number"),
        Map.entry(Constants.FLOAT_TYPE_INFO, "number"),
        Map.entry(Constants.INT_TYPE_INFO, "number"),
        Map.entry(Constants.LONG_TYPE_INFO, "number"),
        Map.entry(Constants.SHORT_TYPE_INFO, "number"),

        Map.entry(Constants.BOXED_BOOLEAN_TYPE_INFO, "boolean"),
        Map.entry(Constants.BOXED_BYTE_TYPE_INFO, "number"),
        Map.entry(Constants.BOXED_CHAR_TYPE_INFO, "string"),
        Map.entry(Constants.BOXED_DOUBLE_TYPE_INFO, "number"),
        Map.entry(Constants.BOXED_FLOAT_TYPE_INFO, "number"),
        Map.entry(Constants.BOXED_INT_TYPE_INFO, "number"),
        Map.entry(Constants.BOXED_LONG_TYPE_INFO, "number"),
        Map.entry(Constants.BOXED_SHORT_TYPE_INFO, "number"),

        Map.entry(Constants.STRING_TYPE_INFO, "string"),
        Map.entry(Constants.VOID_TYPE_INFO, "never")
    );

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
        for (TypeDef typeDef : typeDefs) {
            if (typeDef instanceof EnumDef enumDef) {
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
            } else if (typeDef instanceof ClassDef classDef) {
                var value = new InterfaceExpr(
                    classDef.simpleName(),
                    classDef.typeVariables().stream().map(this::toTypeExpr).toList(),
                    classDef.supertypes().stream().map(this::toTypeExpr).toList(),
                    classDef.components().stream().map(this::toPropertyExpr).toList()
                );
                data.add(Tuple.of(Template.TEMPLATE_INTERFACE, value));
            }
        }

        var file = ctx.createSourceOutput(ctx.getProps().getTypescript().getOutputFileName());
        try (var outputStream = file.openOutputStream();
             var writer = new OutputStreamWriter(outputStream)) {
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
        var typeExprBuilder = new StringBuilder();
        buildTypeExprRecursively(typeInfo, typeExprBuilder);
        return typeExprBuilder.toString();
    }

    private void buildTypeExprRecursively(TypeInfo typeInfo, @SideEffect StringBuilder nameBuilder) { // TODO: abstract up
        if (typeInfo instanceof ConcreteTypeInfo concreteTypeInfo) {
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
        } else if (typeInfo instanceof TypeVariableInfo typeVariableInfo) {
            nameBuilder.append(typeVariableInfo.getName());
        } else if (typeInfo instanceof ArrayTypeInfo arrayTypeInfo) {
            buildTypeExprRecursively(arrayTypeInfo.component(), nameBuilder);
            nameBuilder.append("[]");
        }
    }

    @SuppressWarnings("unused")
    record InterfaceExpr(
        String name,
        List<String> typeParameters,
        List<String> supertypes,
        List<PropertyExpr> properties
    ) {
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

    record PropertyExpr(
        String name,
        String type,
        char propDelimiter,
        boolean optional,
        boolean unionNull,
        boolean unionUndefined
    ) {
    }

    record EnumUnionExpr(
        String name,
        List<String> values
    ) {
        @SuppressWarnings("unused")
        String valuesExpr() {
            return String.join(" | ", values);
        }
    }
}

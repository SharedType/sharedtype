package org.sharedtype.processor.writer;

import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.Constants;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;
import org.sharedtype.processor.support.utils.Tuple;
import org.sharedtype.processor.writer.render.Template;
import org.sharedtype.processor.writer.render.TemplateRenderer;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
final class TypescriptTypeFileWriter implements TypeWriter {
    private static final Map<ConcreteTypeInfo, String> TYPE_NAME_MAPPINGS = Map.ofEntries(
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

    @Inject
    TypescriptTypeFileWriter(Context ctx, TemplateRenderer renderer) {
        this.ctx = ctx;
        elements = ctx.getProcessingEnv().getElementUtils();
        this.renderer = renderer;
        typeNameMappings = new HashMap<>(TYPE_NAME_MAPPINGS);
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getJavaObjectMapType());
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

                data.add(Tuple.of(
                    Template.TEMPLATE_ENUM_UNION,
                    new Model.EnumUnion(enumDef.simpleName(), values)
                ));
            }
        }

        var file = ctx.createSourceOutput(ctx.getProps().getTypescriptOutputFileName());
        try (var outputStream = file.openOutputStream();
             var writer = new OutputStreamWriter(outputStream)) {
            renderer.render(writer, data);
        }
    }

    static final class Model {
        record Interface(
            String name,
            List<String> supertypes,
            List<Property> properties
        ) {
        }

        record Property(
            String name,
            Type type,
            char lineEnding,
            boolean optional
        ) {
        }

        record Type(
            String name,
            boolean unionNull,
            boolean unionUndefined
        ) {
        }

        record EnumUnion(
            String name,
            List<String> values
        ) {
            @SuppressWarnings("unused")
            String valuesExpr() {
                return String.join(" | ", values);
            }
        }
    }
}

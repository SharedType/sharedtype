package org.sharedtype.processor.writer;

import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.Constants;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.writer.render.Template;
import org.sharedtype.processor.writer.render.TemplateRenderer;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    private final Map<ConcreteTypeInfo, String> typeNameMappings;
    private final TemplateRenderer renderer;

    @Inject
    TypescriptTypeFileWriter(Context ctx, TemplateRenderer renderer) {
        this.ctx = ctx;
        this.renderer = renderer;
        typeNameMappings = new HashMap<>(TYPE_NAME_MAPPINGS);
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getJavaObjectMapType());
        renderer.loadTemplates(
            Template.TEMPLATE_INTERFACE,
            Template.TEMPLATE_ENUM_UNION
        );
    }

    @Override
    public void write(List<TypeDef> typeDefs) {
        // TODO
    }
}

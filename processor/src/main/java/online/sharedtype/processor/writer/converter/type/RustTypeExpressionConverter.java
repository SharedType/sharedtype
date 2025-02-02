package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.RenderFlags;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TypeInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

final class RustTypeExpressionConverter extends AbstractTypeExpressionConverter {
    private final Map<TypeInfo, String> typeNameMappings = new HashMap<>(20);
    private final RenderFlags renderFlags;

    RustTypeExpressionConverter(Context ctx) {
        super(
            ctx,
            new ArraySpec("Vec<", ">"),
            null
        );
        this.renderFlags = ctx.getRenderFlags();
        typeNameMappings.put(Constants.BOOLEAN_TYPE_INFO, "bool");
        typeNameMappings.put(Constants.BYTE_TYPE_INFO, "i8");
        typeNameMappings.put(Constants.CHAR_TYPE_INFO, "char");
        typeNameMappings.put(Constants.DOUBLE_TYPE_INFO, "f64");
        typeNameMappings.put(Constants.FLOAT_TYPE_INFO, "f32");
        typeNameMappings.put(Constants.INT_TYPE_INFO, "i32");
        typeNameMappings.put(Constants.LONG_TYPE_INFO, "i64");
        typeNameMappings.put(Constants.SHORT_TYPE_INFO, "i16");

        typeNameMappings.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "bool");
        typeNameMappings.put(Constants.BOXED_BYTE_TYPE_INFO, "i8");
        typeNameMappings.put(Constants.BOXED_CHAR_TYPE_INFO, "char");
        typeNameMappings.put(Constants.BOXED_DOUBLE_TYPE_INFO, "f64");
        typeNameMappings.put(Constants.BOXED_FLOAT_TYPE_INFO, "f32");
        typeNameMappings.put(Constants.BOXED_INT_TYPE_INFO, "i32");
        typeNameMappings.put(Constants.BOXED_LONG_TYPE_INFO, "i64");
        typeNameMappings.put(Constants.BOXED_SHORT_TYPE_INFO, "i16");

        typeNameMappings.put(Constants.STRING_TYPE_INFO, "String");
        typeNameMappings.put(Constants.VOID_TYPE_INFO, "!");
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, "Box<dyn Any>");
    }

    @Override
    void beforeVisitTypeInfo(TypeInfo typeInfo) {
        if (typeInfo.equals(Constants.OBJECT_TYPE_INFO)) {
            renderFlags.setUseRustAny(true);
        }
    }

    @Override
    @Nullable
    String toTypeExpression(TypeInfo typeInfo, @Nullable String fallback) {
        String expr = typeNameMappings.getOrDefault(typeInfo, fallback);
        if (typeInfo instanceof ConcreteTypeInfo && expr != null) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            if (concreteTypeInfo.typeDef() != null && concreteTypeInfo.typeDef().isCyclicReferenced()) {
                expr = String.format("Box<%s>", expr);
            }
        }
        return expr;
    }
}

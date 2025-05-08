package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TargetCodeType;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

final class GoTypeExpressionConverter extends AbstractTypeExpressionConverter {
    private static final String ARRAY_LITERAL = "[]";
    private static final ArraySpec ARRAY_SPEC = new ArraySpec(ARRAY_LITERAL, "");
    private static final MapSpec DEFAULT_MAP_SPEC = new MapSpec("map[", "]", "");
    private static final TypeArgsSpec TYPE_ARGS_SPEC = new TypeArgsSpec("[", ", ", "]");
    private static final TypeArgsSpec ARRAY_TYPE_ARGS_SPEC = new TypeArgsSpec("", "", "");
    final Map<TypeInfo, String> typeNameMappings = new HashMap<>(32);

    public GoTypeExpressionConverter(Context ctx) {
        super(ctx);
        typeNameMappings.put(Constants.BOOLEAN_TYPE_INFO, "bool");
        typeNameMappings.put(Constants.BYTE_TYPE_INFO, "byte");
        typeNameMappings.put(Constants.CHAR_TYPE_INFO, "rune");
        typeNameMappings.put(Constants.DOUBLE_TYPE_INFO, "float64");
        typeNameMappings.put(Constants.FLOAT_TYPE_INFO, "float32");
        typeNameMappings.put(Constants.INT_TYPE_INFO, "int32");
        typeNameMappings.put(Constants.LONG_TYPE_INFO, "int64");
        typeNameMappings.put(Constants.SHORT_TYPE_INFO, "int16");

        typeNameMappings.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "bool");
        typeNameMappings.put(Constants.BOXED_BYTE_TYPE_INFO, "byte");
        typeNameMappings.put(Constants.BOXED_CHAR_TYPE_INFO, "rune");
        typeNameMappings.put(Constants.BOXED_DOUBLE_TYPE_INFO, "float64");
        typeNameMappings.put(Constants.BOXED_FLOAT_TYPE_INFO, "float32");
        typeNameMappings.put(Constants.BOXED_INT_TYPE_INFO, "int32");
        typeNameMappings.put(Constants.BOXED_LONG_TYPE_INFO, "int64");
        typeNameMappings.put(Constants.BOXED_SHORT_TYPE_INFO, "int16");
        typeNameMappings.put(Constants.BIG_DECIMAL_TYPE_INFO, "string");
        typeNameMappings.put(Constants.BIG_INTEGER_TYPE_INFO, "string");

        typeNameMappings.put(Constants.STRING_TYPE_INFO, "string");
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getGo().getJavaObjectMapType());
    }
    @Override
    ArraySpec arraySpec() {
        return ARRAY_SPEC;
    }
    @Override
    MapSpec mapSpec(ConcreteTypeInfo typeInfo) {
        return DEFAULT_MAP_SPEC;
    }
    @Override
    TypeArgsSpec typeArgsSpec(ConcreteTypeInfo typeInfo) {
        if (ARRAY_LITERAL.equals(typeInfo.mappedName(TargetCodeType.GO))) {
            return ARRAY_TYPE_ARGS_SPEC;
        }
        return TYPE_ARGS_SPEC;
    }
    @Override
    String dateTimeTypeExpr(DateTimeInfo dateTimeInfo, Config config) {
        return dateTimeInfo.mappedNameOrDefault(TargetCodeType.GO, config.getGoTargetDatetimeTypeLiteral());
    }
    @Override
    String toTypeExpression(ConcreteTypeInfo typeInfo, String defaultExpr) {
        String expr = typeInfo.mappedName(TargetCodeType.GO);
        if (expr == null) {
            expr = typeNameMappings.getOrDefault(typeInfo, defaultExpr);
        }
        return expr;
    }
}

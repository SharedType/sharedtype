package online.sharedtype.processor.writer;

import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.writer.render.TemplateRenderer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Cause Chung
 */
final class RustTypeFileWriter implements TypeWriter {
    private static final Map<ConcreteTypeInfo, String> PREDEFINED_TYPE_NAME_MAPPINGS;
    static {
        Map<ConcreteTypeInfo, String> tempMap = new HashMap<>(20);
        tempMap.put(Constants.BOOLEAN_TYPE_INFO, "bool");
        tempMap.put(Constants.BYTE_TYPE_INFO, "i8");
        tempMap.put(Constants.CHAR_TYPE_INFO, "char");
        tempMap.put(Constants.DOUBLE_TYPE_INFO, "f64");
        tempMap.put(Constants.FLOAT_TYPE_INFO, "f32");
        tempMap.put(Constants.INT_TYPE_INFO, "i32");
        tempMap.put(Constants.LONG_TYPE_INFO, "i64");
        tempMap.put(Constants.SHORT_TYPE_INFO, "i16");

        tempMap.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "bool");
        tempMap.put(Constants.BOXED_BYTE_TYPE_INFO, "i8");
        tempMap.put(Constants.BOXED_CHAR_TYPE_INFO, "char");
        tempMap.put(Constants.BOXED_DOUBLE_TYPE_INFO, "f64");
        tempMap.put(Constants.BOXED_FLOAT_TYPE_INFO, "f32");
        tempMap.put(Constants.BOXED_INT_TYPE_INFO, "i32");
        tempMap.put(Constants.BOXED_LONG_TYPE_INFO, "i64");
        tempMap.put(Constants.BOXED_SHORT_TYPE_INFO, "i16");

        tempMap.put(Constants.STRING_TYPE_INFO, "String");
        tempMap.put(Constants.VOID_TYPE_INFO, "!");

        PREDEFINED_TYPE_NAME_MAPPINGS = Collections.unmodifiableMap(tempMap);
    }

    private final TemplateRenderer renderer;

    RustTypeFileWriter(TemplateRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void write(List<TypeDef> typeDefs) throws IOException {

    }
}

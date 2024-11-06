package online.sharedtype.processor.parser.type;

import online.sharedtype.SharedType;
import online.sharedtype.processor.parser.TypeDefParser;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.context.Context;

import javax.lang.model.type.TypeMirror;

/**
 * Parse type specific information.
 *
 * @see TypeInfo
 * @see TypeDefParser
 * @author Cause Chung
 */
public interface TypeInfoParser {
    /**
     * Parse type specific information.
     * <p>
     * If a dependent type is not explicitly registered by {@link SharedType},
     * it may not have been resolved in the context by the time of the call.
     * In such case, the caller would resolve the type in another iteration and resolve the type again.
     * </p>
     * <p>
     * An unresolved type is a type whose TypeElement hasn't been processed.
     * So there's no structural information, e.g. name, saved in global context yet.
     * </p>
     */
    TypeInfo parse(TypeMirror typeMirror);

    static TypeInfoParser create(Context ctx) {
        return new TypeInfoParserImpl(ctx);
    }
}

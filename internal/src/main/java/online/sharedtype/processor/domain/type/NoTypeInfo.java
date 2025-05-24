package online.sharedtype.processor.domain.type;

import online.sharedtype.processor.domain.def.TypeDef;

/**
 * Present no type info.
 * @see online.sharedtype.processor.parser.type.TypeInfoParser
 * @author Cause Chung
 */
final class NoTypeInfo implements TypeInfo {
    private static final long serialVersionUID = 1773678739237108430L;

    static final NoTypeInfo INSTANCE = new NoTypeInfo();
    private NoTypeInfo() {}

    @Override
    public void addReferencingType(TypeDef typeDef) {
    }
}

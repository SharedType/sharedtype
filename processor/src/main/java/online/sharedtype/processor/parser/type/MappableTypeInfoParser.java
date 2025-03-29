package online.sharedtype.processor.parser.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.MappableType;
import online.sharedtype.processor.domain.TargetCodeType;
import online.sharedtype.processor.domain.TypeInfo;

import javax.lang.model.type.TypeMirror;
import java.util.Map;

final class MappableTypeInfoParser implements TypeInfoParser {
    private final TypeInfoParser delegate;
    private final Map<String, String> typescriptTypeMappings;
    private final Map<String, String> rustTypeMappings;

    MappableTypeInfoParser(Context ctx, TypeInfoParser delegate) {
        this.delegate = delegate;
        this.typescriptTypeMappings = ctx.getProps().getTypescript().getTypeMappings();
        this.rustTypeMappings = ctx.getProps().getRust().getTypeMappings();
    }

    @Override
    public TypeInfo parse(TypeMirror typeMirror, TypeContext typeContext) {
        TypeInfo typeInfo = delegate.parse(typeMirror, typeContext);

        if (typeInfo instanceof MappableType) {
            MappableType mappableType = (MappableType) typeInfo;
            updateTypeMappings(mappableType, TargetCodeType.TYPESCRIPT, typescriptTypeMappings);
            updateTypeMappings(mappableType, TargetCodeType.RUST, rustTypeMappings);
        }
        return typeInfo;
    }

    private static void updateTypeMappings(MappableType mappableType, TargetCodeType targetCodeType, Map<String, String> typeMappings) {
        String mappedName = typeMappings.get(mappableType.qualifiedName());
        if (mappedName != null) {
            mappableType.addMappedName(targetCodeType, mappedName);
        }
    }
}

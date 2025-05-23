package online.sharedtype.processor.parser.type;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.MappableType;
import online.sharedtype.processor.domain.type.TypeInfo;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

final class MappableTypeInfoParser implements TypeInfoParser {
    private final TypeInfoParser delegate;
    private final Map<String, String> typescriptTypeMappings;
    private final Map<String, String> goTypeMappings;
    private final Map<String, String> rustTypeMappings;

    MappableTypeInfoParser(Context ctx, TypeInfoParser delegate) {
        this.delegate = delegate;
        this.typescriptTypeMappings = ctx.getProps().getTypescript().getTypeMappings();
        this.goTypeMappings = ctx.getProps().getGo().getTypeMappings();
        this.rustTypeMappings = ctx.getProps().getRust().getTypeMappings();
    }

    @Override
    public TypeInfo parse(TypeMirror typeMirror, TypeElement ctxTypeElement) {
        TypeInfo typeInfo = delegate.parse(typeMirror, ctxTypeElement);

        if (typeInfo instanceof MappableType) {
            MappableType mappableType = (MappableType) typeInfo;
            updateTypeMappings(mappableType, SharedType.TargetType.TYPESCRIPT, typescriptTypeMappings);
            updateTypeMappings(mappableType, SharedType.TargetType.GO, goTypeMappings);
            updateTypeMappings(mappableType, SharedType.TargetType.RUST, rustTypeMappings);
        }
        return typeInfo;
    }

    private static void updateTypeMappings(MappableType mappableType, SharedType.TargetType targetType, Map<String, String> typeMappings) {
        String mappedName = typeMappings.get(mappableType.qualifiedName());
        if (mappedName != null) {
            mappableType.addMappedName(targetType, mappedName);
        }
    }
}

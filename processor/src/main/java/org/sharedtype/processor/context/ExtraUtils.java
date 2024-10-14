package org.sharedtype.processor.context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ExtraUtils {
    private final Context ctx;
    private final Types types;
    private final Elements elements;
    private final Set<TypeMirror> arraylikeTypes;

    ExtraUtils(ProcessingEnvironment processingEnv, Props props, Context ctx) {
        this.ctx = ctx;
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        arraylikeTypes = props.getArraylikeTypeQualifiedNames().stream()
                .map(qualifiedName -> types.erasure(elements.getTypeElement(qualifiedName).asType()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isArraylike(TypeMirror typeMirror) {
        for (TypeMirror toArrayType : arraylikeTypes) {
            if (types.isSubtype(types.erasure(typeMirror), toArrayType)) {
                return true;
            }
        }
        return false;
    }

    public List<DeclaredType> getTypeArguments(DeclaredType declaredType) {
        var typeArgs = declaredType.getTypeArguments();
        var list = new ArrayList<DeclaredType>(typeArgs.size());
        for (TypeMirror typeArg : typeArgs) {
            if (typeArg instanceof DeclaredType argDeclaredType) {
                list.add(argDeclaredType);
            } else {
                ctx.error("Type argument %s is not a DeclaredType", typeArg);
            }
        }
        return list;
    }
}

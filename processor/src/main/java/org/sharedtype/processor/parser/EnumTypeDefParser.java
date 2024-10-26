package org.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static org.sharedtype.domain.Constants.STRING_TYPE_INFO;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class EnumTypeDefParser implements TypeDefParser {
    private final Context ctx;

    @Override
    public TypeDef parse(TypeElement typeElement) {
        var config = new Config(typeElement);
        var enumConstants = typeElement.getEnclosedElements().stream()
            .filter(elem -> elem.getKind() == ElementKind.ENUM_CONSTANT)
            .map(elem -> (VariableElement) elem)
            .toList();

        return EnumDef.builder()
            .qualifiedName(config.getQualifiedName())
            .simpleName(config.getName())
            .enumValueInfos(enumConstants.stream().map(this::parseEnumConstant).toList())
            .build();
    }

    private EnumValueInfo parseEnumConstant(VariableElement enumConstant) {
        var type = enumConstant.asType();
        var element = ctx.getProcessingEnv().getTypeUtils().asElement(type);
        return new EnumValueInfo(STRING_TYPE_INFO, enumConstant.getSimpleName().toString());
    }
}

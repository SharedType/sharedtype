package org.jets.processor.parser;

import lombok.RequiredArgsConstructor;
import org.jets.processor.JetsContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class JavaRecordParser implements TypeElementParser {
    @Override
    public TypeInfo parse(TypeElement typeElement, JetsContext ctx) {
        ctx.checkArgument(typeElement.getKind() == ElementKind.RECORD, "Unsupported element kind: " + typeElement.getKind());
        var builder = TypeInfo.builder();
        for (RecordComponentElement recordComponent : typeElement.getRecordComponents()) {
            ctx.info(recordComponent.toString());
        }


        return builder.build();
    }
}

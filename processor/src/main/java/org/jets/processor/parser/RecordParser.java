package org.jets.processor.parser;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;

import lombok.RequiredArgsConstructor;
import org.jets.processor.JetsContext;
import org.jets.processor.domain.ClassInfo;
import org.jets.processor.domain.FieldInfo;
import org.jets.processor.parser.type.TypeMapper;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class RecordParser implements TypeElementParser {
    private final TypeMapper typeMapper;

    @Override
    public ClassInfo parse(TypeElement typeElement, JetsContext ctx) {
        ctx.checkArgument(typeElement.getKind() == ElementKind.RECORD,
                "Unsupported element kind: " + typeElement.getKind());

        var config = new AnnoConfig(typeElement);

        var builder = ClassInfo.builder().name(config.getName());
        var recordComponents = typeElement.getRecordComponents();
        var fields = new ArrayList<FieldInfo>(recordComponents.size());
        for (RecordComponentElement recordComponent : recordComponents) {
            if (config.isComponentExcluded(recordComponent)) {
                continue;
            }
            var fieldInfo = FieldInfo.builder()
                    .name(recordComponent.getSimpleName().toString())
                    .modifiers(recordComponent.getModifiers())
                    .optional(recordComponent.getAnnotation(ctx.getProps().getOptionalAnno()) != null)
                    .type(typeMapper.map(recordComponent.asType(), ctx))
                    .build();
            fields.add(fieldInfo);
        }
        builder.fields(fields);

        if (config.isIncludeGetters()) {
            // TODO
        }

        return builder.build();
    }
}

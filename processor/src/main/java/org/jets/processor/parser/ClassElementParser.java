package org.jets.processor.parser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.AnnoConfig;
import org.jets.processor.context.Context;
import org.jets.processor.domain.ClassInfo;
import org.jets.processor.domain.DefInfo;
import org.jets.processor.domain.FieldInfo;
import org.jets.processor.parser.field.FieldElementParser;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class ClassElementParser implements TypeElementParser {
    private final FieldElementParser fieldElementParser;
    private final Context ctx;

    @Override
    public List<DefInfo> parse(TypeElement typeElement) {
        var config = new AnnoConfig(typeElement); // TODO: validate typeElement's eligibility
        ctx.saveType(config.getQualifiedName(), config.getName());

        var builder = ClassInfo.builder().name(config.getName());
        var fieldElements = typeElement.getEnclosedElements().stream()
                .filter(elem -> elem instanceof VariableElement variableElement && variableElement.getKind() == ElementKind.FIELD)
                .toList();
        var fields = new ArrayList<FieldInfo>(fieldElements.size());
        for (var element : fieldElements) {
            if (config.isComponentExcluded(element)) {
                continue;
            }
            var fieldInfo = FieldInfo.builder()
                    .name(element.getSimpleName().toString())
                    .modifiers(element.getModifiers())
                    .optional(element.getAnnotation(ctx.getProps().getOptionalAnno()) != null)
                    .typeInfo(fieldElementParser.parse((VariableElement) element))
                    .build();
            fields.add(fieldInfo);
        }
        builder.fields(fields);

        if (config.toIncludeGetters()) {
            // TODO
        }

        return List.of(builder.build());
    }
}

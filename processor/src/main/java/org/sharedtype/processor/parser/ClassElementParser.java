package org.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.ClassDef;
import org.sharedtype.processor.domain.FieldInfo;
import org.sharedtype.processor.domain.TypeDef;
import org.sharedtype.processor.domain.TypeVariableInfo;
import org.sharedtype.processor.parser.field.VariableElementParser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class ClassElementParser implements TypeElementParser {
    private final VariableElementParser variableElementParser;
    private final Context ctx;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        var config = new Config(typeElement); // TODO: validate typeElement's eligibility
        ctx.saveType(config.getQualifiedName(), config.getName());

        var builder = ClassDef.builder().name(config.getName());
        builder.typeVariables(parseTypeVariables(typeElement));
        builder.fields(parseFields(typeElement, config));

        if (config.toIncludeGetters()) {
            // TODO
        }

        return List.of(builder.build());
    }

    private List<TypeVariableInfo> parseTypeVariables(TypeElement typeElement) {
        var typeParameters = typeElement.getTypeParameters();
        return typeParameters.stream()
          .map(typeParameterElement -> TypeVariableInfo.builder().name(typeParameterElement.getSimpleName().toString()).build())
          .toList();
    }

    private List<FieldInfo> parseFields(TypeElement typeElement, Config config) {
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
              .typeInfo(variableElementParser.parse(element.asType()))
              .build();
            fields.add(fieldInfo);
        }
        return fields;
    }
}

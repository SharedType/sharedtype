package org.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.ClassDef;
import org.sharedtype.processor.domain.FieldInfo;
import org.sharedtype.processor.domain.TypeDef;
import org.sharedtype.processor.domain.TypeVariableInfo;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class TypeDefParserImpl implements TypeDefParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (ctx.isTypeIgnored(typeElement)) {
            return Collections.emptyList();
        }

        var config = new Config(typeElement); // TODO: validate typeElement's eligibility
        ctx.saveType(config.getQualifiedName(), config.getName());

        var builder = ClassDef.builder().qualifiedName(config.getQualifiedName()).name(config.getName());
        builder.typeVariables(parseTypeVariables(typeElement));
        builder.fields(parseFields(typeElement, config));
        builder.supertypes(parseSupertypes(typeElement));

        // TODO config.includes()

        return List.of(builder.build());
    }

    private List<TypeVariableInfo> parseTypeVariables(TypeElement typeElement) {
        var typeParameters = typeElement.getTypeParameters();
        return typeParameters.stream()
          .map(typeParameterElement -> TypeVariableInfo.builder().name(typeParameterElement.getSimpleName().toString()).build())
          .toList();
    }

    private List<TypeDef> parseSupertypes(TypeElement typeElement) {
        var supertypes = new ArrayList<TypeDef>();
        var superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType declaredType) {
            supertypes.addAll(parse((TypeElement) declaredType.asElement()));
        }

        var interfaceTypes = typeElement.getInterfaces();
        for (TypeMirror interfaceType : interfaceTypes) {
            var declaredType = (DeclaredType) interfaceType;
            supertypes.addAll(parse((TypeElement) declaredType.asElement()));
        }
        return supertypes;
    }

    private List<FieldInfo> parseFields(TypeElement typeElement, Config config) {
        var fieldElements = typeElement.getEnclosedElements().stream()
          .filter(elem -> elem instanceof VariableElement variableElement && variableElement.getKind() == ElementKind.FIELD)
          .toList();
        var fields = new ArrayList<FieldInfo>(fieldElements.size());
        for (var element : fieldElements) {
            if (config.isComponentIgnored(element)) {
                continue;
            }
            var fieldInfo = FieldInfo.builder()
              .name(element.getSimpleName().toString())
              .modifiers(element.getModifiers())
              .optional(element.getAnnotation(ctx.getProps().getOptionalAnno()) != null)
              .type(typeInfoParser.parse(element.asType()))
              .build();
            fields.add(fieldInfo);
        }
        return fields;
    }
}

package org.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import org.sharedtype.annotation.SharedType;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.ClassDef;
import org.sharedtype.processor.domain.FieldComponentInfo;
import org.sharedtype.processor.domain.TypeDef;
import org.sharedtype.processor.domain.TypeVariableInfo;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;

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
        // TODO: cache parsed type info

        var config = new Config(typeElement); // TODO: validate typeElement's eligibility
        ctx.saveType(config.getQualifiedName(), config.getName());

        var builder = ClassDef.builder().qualifiedName(config.getQualifiedName()).name(config.getName());
        builder.typeVariables(parseTypeVariables(typeElement));
        builder.components(parseComponents(typeElement, config));
        builder.supertypes(parseSupertypes(typeElement));

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

    private List<FieldComponentInfo> parseComponents(TypeElement typeElement, Config config) {
        var componentElems = resolveComponents(typeElement, config);

        var fields = new ArrayList<FieldComponentInfo>(componentElems.size());
        for (var element : componentElems) {
            var fieldInfo = FieldComponentInfo.builder()
              .name(element.getSimpleName().toString())
              .modifiers(element.getModifiers())
              .optional(element.getAnnotation(ctx.getProps().getOptionalAnno()) != null)
              .type(typeInfoParser.parse(element.asType()))
              .build();
            fields.add(fieldInfo);
        }
        return fields;
    }

    private List<VariableElement> resolveComponents(TypeElement typeElement, Config config) {
        var componentElems = typeElement.getEnclosedElements().stream()
          .filter(elem -> !config.isComponentIgnored(elem))
          .filter(elem -> elem instanceof VariableElement)
          .map(VariableElement.class::cast)
          .toList();

        var res = new ArrayList<VariableElement>(componentElems.size());
        var namesOfTypes = new NamesOfTypes(componentElems.size());
        for (VariableElement componentElem : componentElems) {
            var type = componentElem.asType();
            var name = componentElem.getSimpleName().toString();
            if (config.includes(SharedType.ComponentType.FIELDS) && componentElem.getKind() == ElementKind.FIELD) {
                if (namesOfTypes.contains(name, type)) {
                    continue;
                }
                res.add(componentElem);
                namesOfTypes.add(name, type);
            }
            if (config.includes(SharedType.ComponentType.ACCESSORS) && isAccessor(componentElem, typeElement)) {
                var baseName = typeElement.getKind() == ElementKind.RECORD ? name : getAccessorBaseName(name);
                if (baseName == null) {
                    continue;
                }
                if (namesOfTypes.contains(baseName, type)) {
                    continue;
                }
                res.add(componentElem);
                namesOfTypes.add(baseName, type);
            }
            // TODO: CONSTANTS
        }
        return res;
    }

    private boolean isAccessor(VariableElement componentElem, TypeElement parentTypeElement) {
        if (componentElem.getKind() != ElementKind.METHOD
          || componentElem.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }
        String name = componentElem.getSimpleName().toString();
        if (parentTypeElement.getKind() != ElementKind.RECORD) {
            if (ctx.getProps().getAccessorGetterPrefixes().stream().noneMatch(name::startsWith)) {
                return false;
            }
        }
        // TODO: check argument count:

        return true;
    }

    @Nullable
    private String getAccessorBaseName(String name) {
        return ctx.getProps().getAccessorGetterPrefixes().stream().filter(name::startsWith).findFirst().orElse(null);
    }

    private final class NamesOfTypes {
        private final Map<String, TypeMirror> namesOfTypes;

        NamesOfTypes(int size) {
            this.namesOfTypes = new HashMap<>(size);
        }

        boolean contains(String name, TypeMirror componentType) {
            var type = namesOfTypes.get(name);
            if (type == null) {
                return false;
            }
            if (!type.equals(componentType)) {
                ctx.error("Components with same name '%s' have different types '%s' and '%s'", name, type, componentType);
                return false;
            }
            return true;
        }

        void add(String name, TypeMirror componentType) {
            namesOfTypes.put(name, componentType);
        }
    }
}

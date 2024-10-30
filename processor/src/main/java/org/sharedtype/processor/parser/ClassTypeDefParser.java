package org.sharedtype.processor.parser;

import org.sharedtype.annotation.SharedType;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.FieldComponentInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.domain.TypeInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.type.TypeInfoParser;
import org.sharedtype.processor.support.annotation.VisibleForTesting;
import org.sharedtype.processor.support.utils.Tuple;
import org.sharedtype.processor.support.utils.Utils;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class ClassTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final Types types;
    private final TypeInfoParser typeInfoParser;

    ClassTypeDefParser(Context ctx, TypeInfoParser typeInfoParser) {
        this.ctx = ctx;
        this.types = ctx.getProcessingEnv().getTypeUtils();
        this.typeInfoParser = typeInfoParser;
    }

    @Override
    public TypeDef parse(TypeElement typeElement) {
        Config config = new Config(typeElement);

        ClassDef.ClassDefBuilder builder = ClassDef.builder().qualifiedName(config.getQualifiedName()).simpleName(config.getName());
        builder.typeVariables(parseTypeVariables(typeElement));
        builder.components(parseComponents(typeElement, config));
        builder.supertypes(parseSupertypes(typeElement));

        return builder.build();
    }

    private List<TypeVariableInfo> parseTypeVariables(TypeElement typeElement) {
        List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();
        return typeParameters.stream()
            .map(typeParameterElement -> TypeVariableInfo.builder().name(typeParameterElement.getSimpleName().toString()).build())
            .collect(Collectors.toList()); // TODO: type bounds
    }

    private List<TypeInfo> parseSupertypes(TypeElement typeElement) {
        List<TypeElement> supertypeElems = new ArrayList<>();
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) superclass;
            supertypeElems.add((TypeElement) declaredType.asElement());
        }

        List<? extends TypeMirror> interfaceTypes = typeElement.getInterfaces();
        for (TypeMirror interfaceType : interfaceTypes) {
            DeclaredType declaredType = (DeclaredType) interfaceType;
            supertypeElems.add((TypeElement) declaredType.asElement());
        }

        List<TypeInfo> res = new ArrayList<>(supertypeElems.size());
        for (TypeElement supertypeElem : supertypeElems) {
            if (!ctx.isTypeIgnored(supertypeElem)) {
                res.add(typeInfoParser.parse(supertypeElem.asType()));
            }
        }
        return res;
    }

    private List<FieldComponentInfo> parseComponents(TypeElement typeElement, Config config) {
        List<Tuple<Element, String>> componentElems = resolveComponents(typeElement, config);

        List<FieldComponentInfo> fields = new ArrayList<FieldComponentInfo>(componentElems.size());
        for (Tuple<Element, String> tuple : componentElems) {
            Element element = tuple.a();
            FieldComponentInfo fieldInfo = FieldComponentInfo.builder()
                .name(tuple.b())
                .modifiers(element.getModifiers())
                .optional(element.getAnnotation(ctx.getProps().getOptionalAnno()) != null)
                .type(typeInfoParser.parse(element.asType()))
                .build();
            fields.add(fieldInfo);
        }
        return fields;
    }

    @VisibleForTesting
    List<Tuple<Element, String>> resolveComponents(TypeElement typeElement, Config config) {
        boolean isRecord = typeElement.getKind() == ElementKind.RECORD;
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<ExecutableElement> recordAccessors = typeElement.getRecordComponents().stream().map(RecordComponentElement::getAccessor).collect(Collectors.toList());

        List<Tuple<Element, String>> res = new ArrayList<>(enclosedElements.size());
        NamesOfTypes namesOfTypes = new NamesOfTypes(enclosedElements.size());
        boolean includeAccessors = config.includes(SharedType.ComponentType.ACCESSORS);
        boolean includeFields = config.includes(SharedType.ComponentType.FIELDS) && !(isRecord && includeAccessors);

        for (Element enclosedElement : enclosedElements) {
            if (config.isComponentIgnored(enclosedElement)) {
                continue;
            }

            TypeMirror type = enclosedElement.asType();
            String name = enclosedElement.getSimpleName().toString();

            if (includeFields && enclosedElement.getKind() == ElementKind.FIELD && enclosedElement instanceof VariableElement variableElem) {
                if (namesOfTypes.contains(name, type)) {
                    continue;
                }
                res.add(Tuple.of(variableElem, name));
                namesOfTypes.add(name, type);
            }

            if (includeAccessors && enclosedElement instanceof ExecutableElement methodElem && isZeroArgNonstaticMethod(methodElem)) {
                boolean explicitAccessor = methodElem.getAnnotation(SharedType.Accessor.class) != null;
                if (!explicitAccessor && isRecord && !recordAccessors.contains(methodElem)) {
                    continue;
                }
                String baseName = getAccessorBaseName(name, isRecord);
                if (baseName == null) {
                    continue;
                }
                TypeMirror returnType = methodElem.getReturnType();
                if (namesOfTypes.contains(baseName, returnType)) {
                    continue;
                }
                res.add(Tuple.of(methodElem, baseName));
                namesOfTypes.add(baseName, returnType);
            }

            // TODO: CONSTANTS
        }

        return res;
    }

    private static boolean isZeroArgNonstaticMethod(ExecutableElement componentElem) {
        if (componentElem.getKind() != ElementKind.METHOD || componentElem.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }
        return componentElem.getParameters().isEmpty();
    }

    @Nullable
    private String getAccessorBaseName(String name, boolean isRecord) {
        for (String accessorGetterPrefix : ctx.getProps().getAccessorGetterPrefixes()) {
            if (name.startsWith(accessorGetterPrefix)) {
                return Utils.substringAndUncapitalize(name, accessorGetterPrefix.length());
            }
        }
        if (isRecord) {
            return name;
        }
        return null;
    }

    private final class NamesOfTypes {
        private final Map<String, TypeMirror> namesOfTypes;

        NamesOfTypes(int size) {
            this.namesOfTypes = new HashMap<>(size);
        }

        boolean contains(String name, TypeMirror componentType) {
            TypeMirror type = namesOfTypes.get(name);
            if (type == null) {
                return false;
            }
            if (!types.isSameType(type, componentType)) {
                ctx.error("Components with same name '%s' have different types '%s' and '%s'", name, type, componentType);
            }
            return true;
        }

        void add(String name, TypeMirror componentType) {
            namesOfTypes.put(name, componentType);
        }
    }
}

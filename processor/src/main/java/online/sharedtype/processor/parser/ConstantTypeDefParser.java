package online.sharedtype.processor.parser;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.value.ValueParser;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
final class ConstantTypeDefParser implements TypeDefParser {
    private static final Set<String> SUPPORTED_ELEMENT_KIND = new HashSet<>(4);
    static {
        SUPPORTED_ELEMENT_KIND.add(ElementKind.CLASS.name());
        SUPPORTED_ELEMENT_KIND.add(ElementKind.INTERFACE.name());
        SUPPORTED_ELEMENT_KIND.add("RECORD");
        SUPPORTED_ELEMENT_KIND.add(ElementKind.ENUM.name());
    }

    private final Context ctx;
    private final ValueParser valueParser;

    @Override
    public List<TypeDef> parse(TypeElement typeElement) {
        if (!SUPPORTED_ELEMENT_KIND.contains(typeElement.getKind().name())) {
            return Collections.emptyList();
        }
        if (ctx.getTrees() == null) {
            ctx.info(typeElement, "Skip parsing constants for type %s, because tree is not available.", typeElement);
            return Collections.emptyList();
        }

        String qualifiedName = typeElement.getQualifiedName().toString();
        List<TypeDef> cachedDefs = ctx.getTypeStore().getTypeDefs(qualifiedName);
        if (cachedDefs == null || cachedDefs.isEmpty()) {
            throw new SharedTypeInternalError("No main type def found for: " + qualifiedName);
        }
        TypeDef mainTypeDef = cachedDefs.get(0);
        if (shouldSkip(mainTypeDef)) {
            return Collections.emptyList();
        }

        Config config = ctx.getTypeStore().getConfig(mainTypeDef);
        if (!config.includes(SharedType.ComponentType.CONSTANTS)) {
            return Collections.emptyList();
        }
        ConstantNamespaceDef constantNamespaceDef = ConstantNamespaceDef.builder().element(typeElement)
            .qualifiedName(qualifiedName)
            .simpleName(config.getSimpleName())
            .build();

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (ctx.isIgnored(enclosedElement)) {
                continue;
            }

            if (enclosedElement.getKind() == ElementKind.FIELD && enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                ValueHolder value = valueParser.resolve(enclosedElement, typeElement);
                ConstantField constantField = ConstantField.builder()
                    .name(enclosedElement.getSimpleName().toString())
                    .element(enclosedElement)
                    .value(value)
                    .build();
                constantNamespaceDef.components().add(constantField);
            }
        }
        if (constantNamespaceDef.components().isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.singletonList(constantNamespaceDef);
    }

    private static boolean shouldSkip(TypeDef mainTypeDef) {
        if (mainTypeDef instanceof ClassDef) {
            ClassDef classDef = (ClassDef) mainTypeDef;
            return !classDef.isAnnotated();
        }
        return false;
    }
}

package org.sharedtype.processor.parser;

import com.sun.source.tree.*;
import lombok.RequiredArgsConstructor;
import org.sharedtype.annotation.SharedType;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.type.TypeInfoParser;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.inject.Inject;
import javax.lang.model.element.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.sharedtype.domain.Constants.STRING_TYPE_INFO;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
final class EnumTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public TypeDef parse(TypeElement typeElement) {
        var config = new Config(typeElement);
        var enclosedElements = typeElement.getEnclosedElements();
        List<VariableElement> enumConstantElems = new ArrayList<>(enclosedElements.size());

        var enumValueMarker = new EnumValueMarker(ctx, config);
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.ENUM_CONSTANT) {
                enumConstantElems.add((VariableElement) enclosedElement);
            } else if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                enumValueMarker.parseConstructor((ExecutableElement) enclosedElement);
            } else if (enclosedElement.getAnnotation(SharedType.EnumValue.class) != null) {
                enumValueMarker.setField((VariableElement) enclosedElement);
            }
        }

        return EnumDef.builder()
            .qualifiedName(config.getQualifiedName())
            .simpleName(config.getName())
            .enumValueInfos(
                enumValueMarker.marked() ? parseEnumConstants(enumConstantElems, enumValueMarker) : useEnumConstantNames(enumConstantElems)
            )
            .build();
    }

    private static List<EnumValueInfo> useEnumConstantNames(List<VariableElement> enumConstants) {
        List<EnumValueInfo> res = new ArrayList<>(enumConstants.size());
        for (VariableElement enumConstant : enumConstants) {
            res.add(new EnumValueInfo(STRING_TYPE_INFO, enumConstant.getSimpleName().toString()));
        }
        return res;
    }

    private List<EnumValueInfo> parseEnumConstants(List<VariableElement> enumConstants, EnumValueMarker enumValueMarker) {
        List<EnumValueInfo> res = new ArrayList<>(enumConstants.size());
        var valueTypeInfo = typeInfoParser.parse(enumValueMarker.enumValueVariableElem.asType());
        var ctorArgIdx = enumValueMarker.matchAndGetConstructorArgIdx();
        if (ctorArgIdx < 0) {
            return Collections.emptyList();
        }
        for (VariableElement enumConstant : enumConstants) {
            var tree = ctx.getTrees().getTree(enumConstant);
            if (tree instanceof VariableTree variableTree) {
                var value = resolveValue(variableTree, ctorArgIdx);
                if (value != null) {
                    res.add(new EnumValueInfo(valueTypeInfo, value));
                }
            } else {
                throw new SharedTypeInternalError(String.format("Unsupported tree, kind: %s, tree: %s, element: %s", tree.getKind(), tree, enumConstant));
            }
        }
        return res;
    }

    private Object resolveValue(VariableTree tree, int ctorArgIdx) {
        var init = tree.getInitializer();
        if (init instanceof NewClassTree newClassTree) {
            try {
                var argTree = newClassTree.getArguments().get(ctorArgIdx);
                if (argTree instanceof LiteralTree argLiteralTree) {
                    return argLiteralTree.getValue();
                } else {
                    ctx.error("Unsupported argument: %s in %s, argIndex: %s. Only literals are supported as enum value."
                        , argTree, tree, ctorArgIdx);
                    return null;
                }
            } catch (IndexOutOfBoundsException e) {
                throw new SharedTypeInternalError(String.format(
                    "Initializer has incorrect number of arguments: %s in tree: %s, argIndex: %s", init, tree, ctorArgIdx));
            }
        }
        throw new SharedTypeInternalError(String.format("Unsupported initializer: %s in tree: %s", init, tree));
    }

    @RequiredArgsConstructor
    static final class EnumValueMarker {
        private final Context ctx;
        private final Config config;
        private List<String> constructorArgNames = Collections.emptyList();
        private int constructorArgIdx = -1;
        private VariableElement enumValueVariableElem;

        void parseConstructor(ExecutableElement constructor) {
            List<? extends VariableElement> parameters = constructor.getParameters();
            constructorArgNames = new ArrayList<>(parameters.size());
            for (int i = 0, n = parameters.size(); i < n; i++) {
                VariableElement arg = parameters.get(i);
                constructorArgNames.add(arg.getSimpleName().toString());
                if (arg.getAnnotation(SharedType.EnumValue.class) != null) {
                    setField(arg);
                    this.constructorArgIdx = i;
                }
            }
        }

        void setField(VariableElement enumValueVariableElem) {
            if (this.enumValueVariableElem != null) {
                ctx.error("Enum '%s' has multiple annotation @%s usage, only one field or constructor parameter is allowed, found on %s and %s",
                    config.getQualifiedName(), SharedType.EnumValue.class, this.enumValueVariableElem, enumValueVariableElem);
            } else {
                this.enumValueVariableElem = enumValueVariableElem;
            }
        }

        /**
         * Return -1 if no constructor parameter can be matched
         */
        int matchAndGetConstructorArgIdx() {
            if (constructorArgIdx >= 0) {
                return constructorArgIdx;
            }

            for (int i = 0, n = constructorArgNames.size(); i < n; i++) {
                if (constructorArgNames.get(i).equals(enumValueVariableElem.getSimpleName().toString())) {
                    return i;
                }
            }

            String lombokSuggestion = "";
            if (constructorArgNames.isEmpty()) {
                lombokSuggestion = "The discovered constructor has 0 parameter, if Lombok is used to generate the constructor," +
                    " please ensure annotation processing of Lombok is executed before SharedType.";
            }

            ctx.error("Enum '%s' has @%s annotated on a field, but no constructor parameter can be matched."
                    + lombokSuggestion
                    + " Please refer to the documentation on how to correctly use mark enum value.",
                config.getQualifiedName(), SharedType.EnumValue.class);
            return -1;
        }

        boolean marked() {
            return enumValueVariableElem != null;
        }
    }
}

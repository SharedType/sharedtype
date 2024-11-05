package org.sharedtype.processor.parser;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import lombok.RequiredArgsConstructor;
import org.sharedtype.annotation.SharedType;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.domain.TypeInfo;
import org.sharedtype.processor.context.Config;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.parser.type.TypeInfoParser;
import org.sharedtype.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.sharedtype.domain.Constants.STRING_TYPE_INFO;

/**
 * Literal values are parsed via {@link Tree} API. It has limitations, see the documentation for more details.
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class EnumTypeDefParser implements TypeDefParser {
    private final Context ctx;
    private final TypeInfoParser typeInfoParser;

    @Override
    public TypeDef parse(TypeElement typeElement) {
        Config config = new Config(typeElement);
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<VariableElement> enumConstantElems = new ArrayList<>(enclosedElements.size());

        EnumValueMarker enumValueMarker = new EnumValueMarker(ctx, config);
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
                enumValueMarker.marked() ? parseEnumConstants(typeElement, enumConstantElems, enumValueMarker) : useEnumConstantNames(enumConstantElems)
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

    private List<EnumValueInfo> parseEnumConstants(TypeElement enumTypeElement, List<VariableElement> enumConstants, EnumValueMarker enumValueMarker) {
        List<EnumValueInfo> res = new ArrayList<>(enumConstants.size());
        TypeInfo valueTypeInfo = typeInfoParser.parse(enumValueMarker.enumValueVariableElem.asType());
        int ctorArgIdx = enumValueMarker.matchAndGetConstructorArgIdx();
        if (ctorArgIdx < 0) {
            return Collections.emptyList();
        }
        for (VariableElement enumConstant : enumConstants) {
            Tree tree = ctx.getTrees().getTree(enumConstant);
            if (tree instanceof VariableTree) {
                VariableTree variableTree = (VariableTree) tree;
                Object value = resolveValue(enumTypeElement, variableTree, ctorArgIdx);
                if (value != null) {
                    res.add(new EnumValueInfo(valueTypeInfo, value));
                }
            } else if (tree == null) {
                ctx.error("Literal value cannot be parsed from enum constant: %s of enum %s, because source tree from the element is null." +
                    " This could mean at the time of the annotation processing, the source tree was not available." +
                    " Is this class from a dependency jar/compiled class file? Please refer to the documentation for more information.",
                    enumConstant, enumTypeElement);
            } else {
                throw new SharedTypeInternalError(String.format(
                    "Unsupported tree during parsing enum %s, kind: %s, tree: %s, element: %s", enumTypeElement, tree.getKind(), tree, enumConstant));
            }
        }
        return res;
    }

    private Object resolveValue(TypeElement enumTypeElement, VariableTree tree, int ctorArgIdx) {
        ExpressionTree init = tree.getInitializer();
        if (init instanceof NewClassTree) {
            NewClassTree newClassTree = (NewClassTree) init;
            try {
                ExpressionTree argTree = newClassTree.getArguments().get(ctorArgIdx);
                if (argTree instanceof LiteralTree) {
                    LiteralTree argLiteralTree = (LiteralTree) argTree;
                    return argLiteralTree.getValue();
                } else {
                    ctx.error("Unsupported argument in enum type %s: %s in %s, argIndex: %s. Only literals are supported as enum value.",
                        enumTypeElement, argTree, tree, ctorArgIdx);
                    return null;
                }
            } catch (IndexOutOfBoundsException e) {
                throw new SharedTypeInternalError(String.format(
                    "Initializer in enum %s has incorrect number of arguments: %s in tree: %s, argIndex: %s", enumTypeElement, init, tree, ctorArgIdx));
            }
        }
        throw new SharedTypeInternalError(String.format("Unsupported initializer in enum %s: %s in tree: %s", enumTypeElement, init, tree));
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
                ctx.error("Enum %s has multiple annotation @%s usage, only one field or constructor parameter is allowed, found on %s and %s",
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
                    " please ensure annotation processing of Lombok is executed before SharedType. Or add explicit constructor." +
                    " Later version of SharedType may infer constructor parameter position by field position without an explicit constructor.";
            }

            ctx.error("Enum %s has @%s annotated on a field, but no constructor parameter can be matched."
                    + lombokSuggestion
                    + " May refer to the documentation on how to correctly mark enum value.",
                config.getQualifiedName(), SharedType.EnumValue.class);
            return -1;
        }

        boolean marked() {
            return enumValueVariableElem != null;
        }
    }
}

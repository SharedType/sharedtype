package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.List;
import java.util.stream.Collectors;

import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.NULL;
import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.QUESTION_MARK;
import static online.sharedtype.processor.context.Props.Typescript.OptionalFieldFormat.UNDEFINED;

final class TypescriptInterfaceConverter implements TemplateDataConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;
    private final char interfacePropertyDelimiter;

    TypescriptInterfaceConverter(Context ctx, TypeExpressionConverter typeExpressionConverter) {
        this.ctx = ctx;
        interfacePropertyDelimiter = ctx.getProps().getTypescript().getInterfacePropertyDelimiter();
        this.typeExpressionConverter = typeExpressionConverter;
    }

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        if (typeDef instanceof ClassDef) {
            ClassDef classDef = (ClassDef) typeDef;
            if (classDef.isMapType()) {
                return false;
            }
            return !classDef.components().isEmpty() || classDef.isDepended();
        }
        return false;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        Config config = ctx.getTypeStore().getConfig(typeDef);
        InterfaceExpr value = new InterfaceExpr(
            classDef.simpleName(),
            classDef.typeVariables().stream().map(typeInfo -> typeExpressionConverter.toTypeExpr(typeInfo, typeDef)).collect(Collectors.toList()),
            classDef.directSupertypes().stream().map(typeInfo1 -> typeExpressionConverter.toTypeExpr(typeInfo1, typeDef)).collect(Collectors.toList()),
            classDef.components().stream().map(field -> toPropertyExpr(field, typeDef, config)).collect(Collectors.toList())
        );

        return Tuple.of(Template.TEMPLATE_TYPESCRIPT_INTERFACE, value);
    }

    private PropertyExpr toPropertyExpr(FieldComponentInfo field, TypeDef contextTypeDef, Config config) {
        boolean isFieldOptional = ConversionUtils.isOptionalField(field);
        return new PropertyExpr(
            field.name(),
            typeExpressionConverter.toTypeExpr(field.type(), contextTypeDef),
            interfacePropertyDelimiter,
            isFieldOptional && config.getTypescriptOptionalFieldFormats().contains(QUESTION_MARK),
            isFieldOptional && config.getTypescriptOptionalFieldFormats().contains(NULL),
            isFieldOptional && config.getTypescriptOptionalFieldFormats().contains(UNDEFINED)
        );
    }

    @RequiredArgsConstructor
    @SuppressWarnings("unused")
    static final class InterfaceExpr {
        final String name;
        final List<String> typeParameters;
        final List<String> supertypes;
        final List<PropertyExpr> properties;

        String typeParametersExpr() {
            if (typeParameters.isEmpty()) {
                return null;
            }
            return String.format("<%s>", String.join(", ", typeParameters));
        }

        String supertypesExpr() {
            if (supertypes.isEmpty()) {
                return null;
            }
            return String.format("extends %s ", String.join(", ", supertypes));
        }
    }

    @RequiredArgsConstructor
    @SuppressWarnings("unused")
    static final class PropertyExpr{
        final String name;
        final String type;
        final char propDelimiter;
        final boolean optional;
        final boolean unionNull;
        final boolean unionUndefined;
    }
}

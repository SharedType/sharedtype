package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.List;
import java.util.stream.Collectors;

final class TypescriptInterfaceConverter implements TemplateDataConverter {
    private final TypeExpressionConverter typeExpressionBuilder;
    private final char interfacePropertyDelimiter;

    TypescriptInterfaceConverter(Context ctx, TypeExpressionConverter typeExpressionConverter) {
        interfacePropertyDelimiter = ctx.getProps().getTypescript().getInterfacePropertyDelimiter();
        this.typeExpressionBuilder = typeExpressionConverter;
    }

    @Override
    public boolean supports(TypeDef typeDef) {
        return typeDef instanceof ClassDef;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ClassDef classDef = (ClassDef) typeDef;
        InterfaceExpr value = new InterfaceExpr(
            classDef.simpleName(),
            classDef.typeVariables().stream().map(typeExpressionBuilder::toTypeExpr).collect(Collectors.toList()),
            classDef.supertypes().stream().map(typeExpressionBuilder::toTypeExpr).collect(Collectors.toList()),
            classDef.components().stream().map(this::toPropertyExpr).collect(Collectors.toList())
        );
        return Tuple.of(Template.TEMPLATE_TYPESCRIPT_INTERFACE, value);
    }

    private PropertyExpr toPropertyExpr(FieldComponentInfo field) {
        return new PropertyExpr(
            field.name(),
            typeExpressionBuilder.toTypeExpr(field.type()),
            interfacePropertyDelimiter,
            field.optional(),
            false,
            false // TODO: more options
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

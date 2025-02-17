package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class ConstantConverter implements TemplateDataConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;
    private final OutputTarget outputTarget;

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof ConstantNamespaceDef;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ConstantNamespaceDef constantNamespaceDef = (ConstantNamespaceDef) typeDef;
        ConstantNamespaceExpr value = new ConstantNamespaceExpr(
            constantNamespaceDef.simpleName(),
            constantNamespaceDef.components().stream().map(field -> toConstantExpr(field, typeDef)).collect(Collectors.toList())
        );

        Config config = ctx.getTypeStore().getConfig(typeDef.qualifiedName());
        if (config == null) {
            throw new SharedTypeInternalError("No config found for: " + typeDef.qualifiedName());
        }
        return Tuple.of(Template.forConstant(outputTarget, config.isConstantNamespaced()), value);
    }

    private ConstantExpr toConstantExpr(ConstantField constantField, TypeDef contextTypeDef) {
        return new ConstantExpr(
            constantField.name(),
            typeExpressionConverter.toTypeExpr(constantField.type(), contextTypeDef),
            LiteralUtils.literalValue(constantField.value())
        );
    }

    @RequiredArgsConstructor
    static final class ConstantNamespaceExpr {
        final String name;
        final List<ConstantExpr> constants;
    }

    @RequiredArgsConstructor
    static final class ConstantExpr {
        final String name;
        final String type;
        final String value;
    }
}

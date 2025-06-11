package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.component.ComponentInfo;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class DefaultConstantConverter extends AbstractConstantConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;
    private final SharedType.TargetType targetType;

    @Override
    public Tuple<Template, AbstractTypeExpr> convert(TypeDef typeDef) {
        ConstantNamespaceDef constantNamespaceDef = (ConstantNamespaceDef) typeDef;
        ConstantNamespaceExpr<ConstantExpr> value = new ConstantNamespaceExpr<>(
            constantNamespaceDef.simpleName(),
            constantNamespaceDef.components().stream().map(field -> toConstantExpr(field, typeDef)).collect(Collectors.toList())
        );

        Config config = ctx.getTypeStore().getConfig(typeDef);
        return Tuple.of(Template.forConstant(targetType, config.isConstantNamespaced()), value);
    }

    private ConstantExpr toConstantExpr(ConstantField constantField, TypeDef contextTypeDef) {
        return new ConstantExpr(
            constantField,
            typeExpressionConverter.toTypeExpr(constantField.value().getValueType(), contextTypeDef),
            constantField.value().literalValue()
        );
    }

    final class ConstantExpr extends AbstractFieldExpr {
        final String type;
        final String value;
        ConstantExpr(ComponentInfo componentInfo, String type, String value) {
            super(componentInfo, targetType);
            this.type = type;
            this.value = value;
        }
    }
}

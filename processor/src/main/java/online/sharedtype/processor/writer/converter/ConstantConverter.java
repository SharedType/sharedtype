package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class ConstantConverter implements TemplateDataConverter {
    private final Context ctx;
    @Nullable
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

        Config config = ctx.getTypeStore().getConfig(typeDef);
        return Tuple.of(Template.forConstant(outputTarget, config.isConstantNamespaced()), value);
    }

    private ConstantExpr toConstantExpr(ConstantField constantField, TypeDef contextTypeDef) {
        return new ConstantExpr(
            constantField.name(),
            typeExpressionConverter == null ? null : typeExpressionConverter.toTypeExpr(constantField.type(), contextTypeDef),
            toConstantValue(constantField)
        );
    }

    private String toConstantValue(ConstantField constantField) {
        if (constantField.type() instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo type = (ConcreteTypeInfo) constantField.type();
            ValueHolder value = constantField.value();
            if (value instanceof EnumConstantValue && outputTarget == OutputTarget.RUST) {
                EnumConstantValue enumConstantValue = (EnumConstantValue) value;
                return String.format("%s::%s",type.simpleName(), enumConstantValue.getEnumConstantName());
            }
        }
        return constantField.value().literalValue();
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

package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.component.ComponentInfo;
import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;

import online.sharedtype.processor.support.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class ConstantConverter implements TemplateDataConverter {
    private final Context ctx;
    private final TypeExpressionConverter typeExpressionConverter;
    private final SharedType.TargetType targetType;

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof ConstantNamespaceDef;
    }

    @Override
    public Tuple<Template, AbstractTypeExpr> convert(TypeDef typeDef) {
        ConstantNamespaceDef constantNamespaceDef = (ConstantNamespaceDef) typeDef;
        ConstantNamespaceExpr value = new ConstantNamespaceExpr(
            constantNamespaceDef.simpleName(),
            constantNamespaceDef.components().stream().map(field -> toConstantExpr(field, typeDef)).collect(Collectors.toList())
        );

        Config config = ctx.getTypeStore().getConfig(typeDef);
        return Tuple.of(Template.forConstant(targetType, config.isConstantNamespaced()), value);
    }

    private ConstantExpr toConstantExpr(ConstantField constantField, TypeDef contextTypeDef) {
        return new ConstantExpr(
            constantField,
            toConstantTypeExpr(constantField, contextTypeDef),
            toConstantValue(constantField)
        );
    }

    private String toConstantTypeExpr(ConstantField constantField, TypeDef contextTypeDef) {
        if (targetType == SharedType.TargetType.RUST && constantField.value() instanceof EnumConstantValue) {
            EnumConstantValue enumConstantValue = (EnumConstantValue) constantField.value();
            ConcreteTypeInfo enumTypeInfo = enumConstantValue.getEnumType();
            EnumDef enumTypeDef = (EnumDef) enumTypeInfo.typeDef();
            if (enumTypeDef.hasComponentValueType() && ctx.getProps().getRust().hasEnumValueTypeAlias()) {
                return enumTypeDef.valueTypeAlias();
            }
        }

        return typeExpressionConverter.toTypeExpr(constantField.value().getValueType(), contextTypeDef);
    }

    private String toConstantValue(ConstantField constantField) {
        if (targetType == SharedType.TargetType.RUST) {
            ConcreteTypeInfo type = constantField.value().getValueType();
            ValueHolder value = constantField.value();
            if (value instanceof EnumConstantValue && value.getValueType().getKind() == ConcreteTypeInfo.Kind.ENUM) {
                EnumConstantValue enumConstantValue = (EnumConstantValue) value;
                return String.format("%s::%s",type.simpleName(), enumConstantValue.getEnumConstantName());
            }
        }
        return constantField.value().literalValue();
    }

    @RequiredArgsConstructor
    static final class ConstantNamespaceExpr extends AbstractTypeExpr {
        final String name;
        final List<ConstantExpr> constants;
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

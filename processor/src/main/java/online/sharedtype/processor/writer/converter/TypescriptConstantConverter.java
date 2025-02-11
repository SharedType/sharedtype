package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ConstantField;
import online.sharedtype.processor.domain.ConstantNamespaceDef;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class TypescriptConstantConverter implements TemplateDataConverter {
    private final Context ctx;

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof ConstantNamespaceDef;
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        ConstantNamespaceDef constantNamespaceDef = (ConstantNamespaceDef) typeDef;
        ConstantNamespaceExpr value = new ConstantNamespaceExpr(
            constantNamespaceDef.simpleName(),
            constantNamespaceDef.components().stream().map(TypescriptConstantConverter::toConstantExpr).collect(Collectors.toList())
        );

        return Tuple.of(
            ctx.getProps().getTypescript().isConstantInline() ? Template.TEMPLATE_TYPESCRIPT_CONSTANT_INLINE : Template.TEMPLATE_TYPESCRIPT_CONSTANT,
            value
        );
    }

    private static ConstantExpr toConstantExpr(ConstantField constantField) {
        return new ConstantExpr(
            constantField.name(),
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
        final String value;
    }
}

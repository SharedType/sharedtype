package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.TypeDef;

import java.util.List;

@RequiredArgsConstructor
abstract class AbstractConstantConverter implements TemplateDataConverter {
    @Override
    public final boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof ConstantNamespaceDef;
    }

    @RequiredArgsConstructor
    static final class ConstantNamespaceExpr<A extends AbstractFieldExpr> extends AbstractTypeExpr {
        final String name;
        final List<A> constants;
    }
}

package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.support.annotation.SideEffect;

interface TypeExpressionBuilder {
    void buildTypeExpr(ConcreteTypeInfo typeInfo, @SideEffect StringBuilder exprBuilder);
    void buildArrayExprPrefix(ArrayTypeInfo typeInfo, @SideEffect StringBuilder exprBuilder);
    void buildArrayExprSuffix(ArrayTypeInfo typeInfo, @SideEffect StringBuilder exprBuilder);
}

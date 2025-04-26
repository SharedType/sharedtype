package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.component.ConstantField;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.annotation.SideEffect;

import javax.lang.model.element.Element;
import java.util.List;

final class ConstantEnumValueResolver implements TypeResolver {
    @Override
    public List<TypeDef> resolve(@SideEffect("constant values") List<TypeDef> typeDefs) {
        for (TypeDef typeDef : typeDefs) {
            if (typeDef instanceof ConstantNamespaceDef) {
                ConstantNamespaceDef constantNamespaceDef = (ConstantNamespaceDef) typeDef;
                for (ConstantField component : constantNamespaceDef.components()) {
                    if (component.value() instanceof Element) {
                        resolveConstantValue(component);
                    }
                }
            }
        }
        return typeDefs;
    }

    private void resolveConstantValue(@SideEffect ConstantField constantField) {

    }
}

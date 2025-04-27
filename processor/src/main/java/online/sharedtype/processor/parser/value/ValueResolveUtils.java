package online.sharedtype.processor.parser.value;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@UtilityClass
final class ValueResolveUtils {
    @Nullable
    static TypeElement getEnclosingTypeElement(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement instanceof TypeElement) {
            return (TypeElement) enclosingElement;
        }
        return null;
    }
}

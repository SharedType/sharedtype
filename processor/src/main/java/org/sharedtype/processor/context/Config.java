package org.sharedtype.processor.context;

import lombok.Getter;
import org.sharedtype.annotation.SharedType;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Config wrappers.
 *
 * @author Cause Chung
 */
public final class Config {
    private final SharedType anno;
    @Getter
    private final String name;
    @Getter
    private final String qualifiedName;
    private final Set<SharedType.ComponentType> includedComponentTypes;

    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnoContainer {
        SharedType anno() default @SharedType;
    }

    @AnnoContainer
    private static class DummyDefault {
    }

    public Config(TypeElement typeElement) {
        String simpleName = typeElement.getSimpleName().toString();
        SharedType annoFromType = typeElement.getAnnotation(SharedType.class);
        this.anno = annoFromType == null ? DummyDefault.class.getAnnotation(AnnoContainer.class).anno() : annoFromType;
        this.name = anno.name().isEmpty() ? simpleName : anno.name();
        this.qualifiedName = typeElement.getQualifiedName().toString();
        this.includedComponentTypes = EnumSet.copyOf(Arrays.asList(anno.includes()));
    }

    public boolean includes(SharedType.ComponentType componentType) {
        return includedComponentTypes.contains(componentType);
    }
}

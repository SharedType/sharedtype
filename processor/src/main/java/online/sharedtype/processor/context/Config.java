package online.sharedtype.processor.context;

import lombok.Getter;
import online.sharedtype.SharedType;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Config wrappers.
 *
 * @author Cause Chung
 */
public final class Config {
    @Getter
    private final SharedType anno;
    @Getter
    private final String simpleName;
    @Getter
    private final String qualifiedName;
    private final Set<SharedType.ComponentType> includedComponentTypes;

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnoContainer {
        SharedType anno() default @SharedType;
    }

    @AnnoContainer
    static final class DummyDefault {
    }

    public Config(TypeElement typeElement) {
        String simpleName = typeElement.getSimpleName().toString();
        SharedType annoFromType = typeElement.getAnnotation(SharedType.class);
        this.anno = annoFromType == null ? DummyDefault.class.getAnnotation(AnnoContainer.class).anno() : annoFromType;
        this.simpleName = anno.name().isEmpty() ? simpleName : anno.name();
        this.qualifiedName = typeElement.getQualifiedName().toString();
        List<SharedType.ComponentType> includedCompTypes = Arrays.asList(anno.includes());
        this.includedComponentTypes = includedCompTypes.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(includedCompTypes);
    }

    public boolean includes(SharedType.ComponentType componentType) {
        return includedComponentTypes.contains(componentType);
    }
}

package online.sharedtype.processor.domain.component;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.def.TypeDef;

import java.io.Serializable;
import java.util.List;

/**
 * Represents internal components in a {@link TypeDef}.
 *
 * @author Cause Chung
 */
public interface ComponentInfo extends Serializable {
    boolean resolved();
    String name();
    List<String> getTagLiterals(SharedType.TargetType targetType);
}

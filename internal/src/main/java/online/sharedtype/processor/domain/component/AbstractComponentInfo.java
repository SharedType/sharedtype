package online.sharedtype.processor.domain.component;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import online.sharedtype.SharedType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuperBuilder(toBuilder = true)
public abstract class AbstractComponentInfo implements ComponentInfo {
    private static final long serialVersionUID = -3498751865425579350L;
    @Builder.Default
    private final Map<SharedType.TargetType, List<String>> tagLiterals = Collections.emptyMap();
}

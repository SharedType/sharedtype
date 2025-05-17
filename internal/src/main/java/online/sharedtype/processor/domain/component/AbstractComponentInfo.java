package online.sharedtype.processor.domain.component;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import online.sharedtype.SharedType;

import javax.lang.model.element.Element;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuperBuilder(toBuilder = true)
public abstract class AbstractComponentInfo implements ComponentInfo {
    private static final long serialVersionUID = -3498751865425579350L;

    @Getter
    private transient final Element element;
    @Setter @Builder.Default
    private Map<SharedType.TargetType, List<String>> tagLiterals = Collections.emptyMap();

    public final List<String> getTagLiterals(SharedType.TargetType targetType) {
        return tagLiterals.getOrDefault(targetType, Collections.emptyList());
    }
}

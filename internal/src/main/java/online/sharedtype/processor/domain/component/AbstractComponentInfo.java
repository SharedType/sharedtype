package online.sharedtype.processor.domain.component;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import online.sharedtype.SharedType;

import javax.lang.model.element.Element;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(of = {"element", "name"})
@SuperBuilder(toBuilder = true)
public abstract class AbstractComponentInfo implements ComponentInfo {
    private static final long serialVersionUID = -3498751865425579350L;

    @Getter
    private transient final Element element;
    private final String name;
    @Setter @Builder.Default
    private Map<SharedType.TargetType, List<TagLiteralContainer>> tagLiterals = Collections.emptyMap();

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final List<TagLiteralContainer> getTagLiterals(SharedType.TargetType targetType) {
        return tagLiterals.getOrDefault(targetType, Collections.emptyList());
    }
}

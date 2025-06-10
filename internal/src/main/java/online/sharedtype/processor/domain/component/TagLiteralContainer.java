package online.sharedtype.processor.domain.component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import online.sharedtype.SharedType;

import java.io.Serializable;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public final class TagLiteralContainer implements Serializable {
    private static final long serialVersionUID = 916098025299922397L;
    private final List<String> contents;
    private final SharedType.TagPosition position;
}

package online.sharedtype.processor.context;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Name;

@EqualsAndHashCode
@RequiredArgsConstructor
final class MockName implements Name {
    private final String name;

    @Override
    public boolean contentEquals(CharSequence cs) {
        return name.contentEquals(cs);
    }
    @Override
    public int length() {
        return name.length();
    }
    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }
    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public String toString() {
        return name;
    }
}

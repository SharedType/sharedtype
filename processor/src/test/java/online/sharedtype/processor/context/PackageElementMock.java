package online.sharedtype.processor.context;

import javax.lang.model.element.PackageElement;
import javax.lang.model.type.NoType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class PackageElementMock extends AbstractElementMock<PackageElement, NoType, PackageElementMock> {
    PackageElementMock(String qualifiedName, Context ctx) {
        super(mock(PackageElement.class, qualifiedName), mock(NoType.class), ctx);
        setQualifiedName(element, qualifiedName);
    }
}

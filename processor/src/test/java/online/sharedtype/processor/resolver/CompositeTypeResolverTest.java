package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.TypeDef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class CompositeTypeResolverTest {
    private @Mock TypeResolver delegate1;
    private @Mock TypeResolver delegate2;
    private CompositeTypeResolver resolver;

    @BeforeEach
    void setup() {
        resolver = new CompositeTypeResolver(delegate1, delegate2);
    }

    @Test
    void callInOrder() {
        List<TypeDef> input = List.of(ClassDef.builder().qualifiedName("a.A").build());
        List<TypeDef> out1 = List.of(ClassDef.builder().qualifiedName("a.B").build());
        List<TypeDef> out2 = List.of(ClassDef.builder().qualifiedName("a.C").build());
        when(delegate1.resolve(input)).thenReturn(out1);
        when(delegate2.resolve(out1)).thenReturn(out2);

        assertThat(resolver.resolve(input)).isSameAs(out2);
    }
}

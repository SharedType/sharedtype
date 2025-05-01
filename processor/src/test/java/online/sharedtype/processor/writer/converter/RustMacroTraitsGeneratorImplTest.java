package online.sharedtype.processor.writer.converter;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TestUtils;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.EnumDef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class RustMacroTraitsGeneratorImplTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustMacroTraitsGenerator generator = new RustMacroTraitsGeneratorImpl(ctxMocks.getContext());


    private final ClassDef typeDef = ClassDef.builder().build();
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        when(ctxMocks.getContext().getTypeStore().getConfig(typeDef)).thenReturn(config);
    }

    @Test
    void genMacroTraits() {
        var anno = TestUtils.defaultSharedTypeAnnotation();
        when(config.getAnno()).thenReturn(anno);
        when(anno.rustMacroTraits()).thenReturn(new String[]{"PartialEq", "Clone"});
        assertThat(generator.generate(typeDef)).containsExactly("Debug", "PartialEq", "Clone");
    }
}

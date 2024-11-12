package online.sharedtype.processor.writer.render;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.support.utils.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class MustacheTemplateRendererTest {
    private @Mock MustacheFactory mf;
    private MustacheTemplateRenderer renderer;

    private @Mock Mustache compiledMustache;
    private @Mock Writer writer;
    private final Template template = new Template(OutputTarget.GO, "test");

    @BeforeEach
    void setUp() {
        renderer = new MustacheTemplateRenderer(mf);
    }

    @Test
    void loadTemplatesAndRender() {
        when(mf.compile("templates/go/test.mustache")).thenReturn(compiledMustache);

        renderer.render(writer, Collections.singletonList(Tuple.of(template, new HashMap<>())));
        verify(compiledMustache).execute(writer, new HashMap<>());
    }

    @Test
    void errorIfTemplateNotLoaded() {
        assertThatThrownBy(() -> renderer.render(writer, Collections.singletonList(Tuple.of(template, new HashMap<>()))))
            .hasMessageContaining("Template not found");
    }
}

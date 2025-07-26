package online.sharedtype.exec.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class SharedTypeDiagnosticListenerTest {
    private @Mock Diagnostic<JavaFileObject> diagnostic;
    private @Mock Logger log;
    private final Path baseDir = Paths.get("/test/project/dir");
    private SimpleDiagnosticListener listener;

    private @Mock JavaFileObject source;

    @BeforeEach
    void setup() {
        listener = new SimpleDiagnosticListener(log, baseDir);
    }

    @Test
    void skipIfNoSourceCode() {
        when(diagnostic.getSource()).thenReturn(null);

        var sb = new StringBuilder();
        listener.addSourceInfo(sb, diagnostic);
        assertThat(sb.toString()).isEmpty();
    }

    @Test
    void printSourceInfo() {
        when(diagnostic.getSource()).thenReturn(source);
        when(source.getName()).thenReturn("/test/project/dir/src/test.java");
        when(diagnostic.getLineNumber()).thenReturn(69L);
        when(diagnostic.getColumnNumber()).thenReturn(102L);

        var sb = new StringBuilder();
        listener.addSourceInfo(sb, diagnostic);
        assertThat(sb.toString()).isEqualTo("src/test.java:69:102 ");
    }
}

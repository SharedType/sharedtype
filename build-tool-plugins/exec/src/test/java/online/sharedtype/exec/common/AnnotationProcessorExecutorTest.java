package online.sharedtype.exec.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class AnnotationProcessorExecutorTest {
    private @Mock Processor processor;
    private @Mock Logger log;
    private @Mock DependencyResolver dependencyResolver;
    private AnnotationProcessorExecutor executor;
    @TempDir
    private Path projectBaseDir;
    private Path outputDir;
    private Path sourceDir;

    @BeforeEach
    void setup() throws Exception {
        sourceDir = projectBaseDir.resolve("src/main/java");
        Files.createDirectories(sourceDir);
        outputDir = projectBaseDir.resolve("generated-sources");
        copySampleSourceToSourceDir(sourceDir);
        Files.createDirectory(outputDir);
        executor = new AnnotationProcessorExecutor(processor, log, dependencyResolver);

        when(processor.getSupportedSourceVersion()).thenReturn(SourceVersion.RELEASE_21);
        when(processor.getSupportedAnnotationTypes()).thenReturn(Collections.singleton("online.sharedtype.exec.common.SampleAnno"));
        when(processor.process(any(), any())).then(invocation -> {
            RoundEnvironment env = invocation.getArgument(1);
            if (env.processingOver()) {
                Files.write(outputDir.resolve("out.txt"), "OK".getBytes());
            }
            return true;
        });
    }

    @Test
    void execute() throws Exception {
        executor.execute(projectBaseDir, outputDir, List.of(sourceDir, Paths.get("not-exist")), "UTF-8", Collections.singleton("-proc:only"));
        verify(processor).init(any());
        verify(processor, times(2)).process(any(), any());
        assertThat(Files.readString(outputDir.resolve("out.txt"))).isEqualTo("OK");
    }

    private static void copySampleSourceToSourceDir(Path sourceDir) throws Exception {
        try (var input = AnnotationProcessorExecutor.class.getClassLoader().getResourceAsStream("SampleSource.java")) {
            assertThat(input).isNotNull();
            Files.copy(input, sourceDir.resolve("SampleSource.java"));
        }
    }
}

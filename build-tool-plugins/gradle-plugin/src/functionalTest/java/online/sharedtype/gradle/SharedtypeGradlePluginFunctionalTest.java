package online.sharedtype.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

final class SharedtypeGradlePluginFunctionalTest {
    @TempDir
    private Path projectDir;

    @BeforeEach
    void setup() throws Exception {
        Files.createFile(projectDir.resolve("settings.gradle"));
        copyResourceToProjectDir("build.gradle");
        copyResourceToProjectDir("sharedtype-test.properties");
    }

    @Test
    void canRunTask() {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withArguments("--stacktrace", "--info", "stypeGen");
        runner.withPluginClasspath();
        runner.withProjectDir(projectDir.toFile());
        BuildResult result = runner.build();

        assertThat(result.getOutput()).contains("");
    }

    private void copyResourceToProjectDir(String resourceName) throws Exception {
        try (var input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(input).isNotNull();
            Files.copy(input, projectDir.resolve(resourceName));
        }
    }
}

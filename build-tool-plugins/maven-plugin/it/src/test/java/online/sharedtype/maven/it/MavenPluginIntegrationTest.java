package online.sharedtype.maven.it;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

final class MavenPluginIntegrationTest {
    @Test
    void verifyGeneratedFile() throws Exception {
        var expectedContent = readResourceContent("expected-types.ts");
        var generatedFileContent = readResourceContent("types.ts");

        assertThat(generatedFileContent).isEqualTo(expectedContent);
    }

    static String readResourceContent(String path) throws Exception {
        var resource = MavenPluginIntegrationTest.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new RuntimeException("Resource not found: " + path);
        }
        return Files.readString(Path.of(resource.toURI()));
    }
}

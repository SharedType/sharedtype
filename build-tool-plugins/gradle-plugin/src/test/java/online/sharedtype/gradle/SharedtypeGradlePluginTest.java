package online.sharedtype.gradle;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class SharedtypeGradlePluginTest {
    @Test
    void pluginRegistersATask() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("org.example.greeting");

        // Verify the result
        assertNotNull(project.getTasks().findByName("greeting"));
    }
}

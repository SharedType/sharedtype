package online.sharedtype.gradle;

import org.gradle.api.Project;
import org.gradle.api.Plugin;

/**
 * @author Cause Chung
 */
final class SharedtypeGradlePlugin implements Plugin<Project> {
    private static final String EXTENSION_METHOD_NAME = "sharedtype";
    private static final String TASK_NAME = "stypeGen";

    public void apply(Project project) {
        var extension = new SharedtypeConfigExtension();
        project.getExtensions().add(EXTENSION_METHOD_NAME, extension);
        project.getTasks().register(TASK_NAME, SharedtypeGenTask.class, task -> task.setExtension(extension));
    }
}

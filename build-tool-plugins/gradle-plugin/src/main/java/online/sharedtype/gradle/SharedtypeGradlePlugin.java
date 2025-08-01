package online.sharedtype.gradle;

import org.gradle.api.Project;
import org.gradle.api.Plugin;

/**
 * @author Cause Chung
 */
final class SharedtypeGradlePlugin implements Plugin<Project> {
    private static final String EXTENSION_METHOD_NAME = "sharedtype";

    public void apply(Project project) {
        SharedtypeConfigExtension extension = new SharedtypeConfigExtension();
        project.getExtensions().add(EXTENSION_METHOD_NAME, extension);
        project.getTasks().register(SharedtypeGenTask.TASK_NAME, SharedtypeGenTask.class, task -> {
            task.setExtension(extension);
            task.setDescription("Generate shared types in target languages. See https://github.com/SharedType/sharedtype");
            task.setGroup("SharedType");
        });
    }
}

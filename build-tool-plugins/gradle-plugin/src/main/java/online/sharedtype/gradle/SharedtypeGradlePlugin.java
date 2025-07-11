package online.sharedtype.gradle;

import org.gradle.api.Project;
import org.gradle.api.Plugin;

public class SharedtypeGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        // Register a task
        project.getTasks().register("greeting", task -> {
            task.doLast(s -> System.out.println("Hello from plugin 'org.example.greeting'"));
        });
    }
}

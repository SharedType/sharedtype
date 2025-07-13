package online.sharedtype.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

/**
 * @author Cause Chung
 */
class SharedtypeGenTask extends DefaultTask {
    private final Project project;
    private SharedtypeConfigExtension extension;

    @Inject
    public SharedtypeGenTask(Project project) {
        super();
        this.project = project;
    }
    void setExtension(SharedtypeConfigExtension extension) {
        this.extension = extension;
    }

    @TaskAction
    void action() {
        System.out.println("####################################");
        System.out.println(extension.properties);
        System.out.println(extension.propertyFile);
        System.out.println(extension.outputDirectory);
        System.out.println(project);
    }
}

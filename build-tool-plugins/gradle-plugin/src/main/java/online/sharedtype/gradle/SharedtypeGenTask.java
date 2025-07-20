package online.sharedtype.gradle;

import online.sharedtype.exec.common.AnnotationProcessorExecutor;
import online.sharedtype.exec.common.SharedTypeApCompilerOptions;
import online.sharedtype.processor.SharedTypeAnnotationProcessor;
import online.sharedtype.processor.support.exception.SharedTypeException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cause Chung
 */
class SharedtypeGenTask extends DefaultTask {
    static final String TASK_NAME = "stypeGen";
    private static final String DEFAULT_OUTPUT_DIRECTORY = "generated-sources";
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
        try {
            execute();
        } catch (Exception e) {
            throw new SharedTypeException(String.format("Failed to execute task '%s'", TASK_NAME), e);
        }
    }

    private void execute() throws Exception {
        JavaPluginExtension javaPluginExtension = project.getExtensions().findByType(JavaPluginExtension.class);
        if (javaPluginExtension == null) {
            throw new UnsupportedOperationException("Could not find JavaPluginExtension, only Java projects are supported.");
        }
        List<Path> sourceDirs = javaPluginExtension.getSourceSets().stream()
            .flatMap(srcSet -> srcSet.getAllSource().getSrcDirs().stream())
            .map(File::toPath)
            .filter(path -> path.getFileName().toString().endsWith(".java"))
            .collect(Collectors.toList());
        List<File> classpathDependencies = javaPluginExtension.getSourceSets().stream()
            .flatMap(srcSet -> srcSet.getCompileClasspath().getFiles().stream())
            .collect(Collectors.toList());

        SharedTypeAnnotationProcessor processor = new SharedTypeAnnotationProcessor();
        processor.setUserProps(extension.getProperties());
        AnnotationProcessorExecutor executor = new AnnotationProcessorExecutor(
            processor,
            new GradleLoggerAdaptor(project.getLogger()),
            () -> classpathDependencies
        );

        executor.execute(
            project.getProjectDir().toPath(),
            resolveOutputDirectory().toPath(),
            sourceDirs,
            extension.getSourceEncoding(),
            new SharedTypeApCompilerOptions(extension.getPropertyFile()).toList()
        );
    }

    private File resolveOutputDirectory() {
        return extension.getOutputDirectory() != null
            ? extension.getOutputDirectory()
            : project.getLayout().getBuildDirectory().dir(DEFAULT_OUTPUT_DIRECTORY).get().getAsFile();
    }
}

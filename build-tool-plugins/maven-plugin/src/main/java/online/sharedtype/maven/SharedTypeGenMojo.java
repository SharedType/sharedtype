package online.sharedtype.maven;

import online.sharedtype.SharedType;
import online.sharedtype.exec.common.AnnotationProcessorExecutor;
import online.sharedtype.exec.common.SharedTypeApCompilerOptions;
import online.sharedtype.processor.SharedTypeAnnotationProcessor;
import online.sharedtype.processor.support.annotation.Nullable;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;

import javax.inject.Inject;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Generate types from {@link SharedType} annotated classes.
 * See <a href="https://github.com/SharedType/sharedtype">SharedType</a> for details.
 *
 * @author Cause Chung
 */
@Mojo(name = "gen")
public final class SharedTypeGenMojo extends AbstractMojo {
    private static final String PROP_PROJECT_SOURCE_ENCODING = "project.build.sourceEncoding";
    private @Inject RepositorySystem repositorySystem;
    private @Inject MavenSession session;
    private @Inject MavenProject project;

    /**
     * Output directory for generated types. Defaults to '${project.build.directory}/generated-sources'.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources")
    private String outputDirectory;

    /**
     * The path of file 'sharedtype.properties'. If not provided, default values will be used. If provided, the file must exist.
     * User-provided properties will be checked in below order:
     * 1. 'sharedtype.properties' file set by this config, 2. individual properties set by this plugin's 'properties' config, 3. System properties.
     */
    @Nullable
    @Parameter
    private String propertyFile;

    /**
     * Sharedtype properties. See doc for all the property entries. User-provided properties will be checked in below order:
     * 1. 'sharedtype.properties' file, 2. this config, 3. System properties.
     */
    @Nullable
    @Parameter
    private Map<String, String> properties;

    @Override
    public void execute() throws MojoExecutionException {
        SharedTypeAnnotationProcessor processor = new SharedTypeAnnotationProcessor();
        processor.setUserProps(properties);
        AnnotationProcessorExecutor apExecutor = new AnnotationProcessorExecutor(
            processor,
            new MavenLoggerAdaptor(getLog()),
            new MavenDependencyResolver(repositorySystem, session, project)
        );
        try {
            apExecutor.execute(
                project.getBasedir().toPath(),
                Paths.get(outputDirectory),
                project.getCompileSourceRoots(),
                project.getProperties().getProperty(PROP_PROJECT_SOURCE_ENCODING),
                new SharedTypeApCompilerOptions(propertyFile).toList()
            );
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
    }
}

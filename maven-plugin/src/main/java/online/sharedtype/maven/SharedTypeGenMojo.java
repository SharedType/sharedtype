package online.sharedtype.maven;

import online.sharedtype.SharedType;
import online.sharedtype.processor.SharedTypeAnnotationProcessor;
import online.sharedtype.processor.support.annotation.Nullable;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;

import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Generate types from {@link SharedType} annotated classes.
 * See <a href="https://github.com/SharedType/sharedtype">SharedType</a> for details.
 * @author Cause Chung
 */
@Mojo(name = "gen")
public final class SharedTypeGenMojo extends AbstractMojo {
    private static final List<String> DEFAULT_COMPILER_OPTIONS = Arrays.asList("-proc:only", "-Asharedtype.enabled=true");
    private @Inject RepositorySystem repositorySystem;

    @Inject
    private MavenSession session;

    @Inject
    private MavenProject project;

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
    public void execute() throws MojoExecutionException, MojoFailureException {
        SharedTypeDiagnosticListener diagnosticListener = new SharedTypeDiagnosticListener(getLog(), project.getBasedir().toPath());
        JavaCompiler compiler = getJavaCompiler();

        DependencyResolver dependencyResolver = new DependencyResolver(repositorySystem, session, project);
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, getCharset());
        try {
            Path outputDirPath = Paths.get(outputDirectory);
            if (Files.notExists(outputDirPath)) {
                Files.createDirectories(outputDirPath);
            }
            fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singleton(outputDirPath.toFile()));
            fileManager.setLocation(StandardLocation.CLASS_PATH, dependencyResolver.getSourceDependencies());
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
        Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromFiles(walkAllSourceFiles());

        try(SharedTypeLogger logger = new SharedTypeLogger(getLog())) {
            JavaCompiler.CompilationTask task = compiler.getTask(logger, fileManager, diagnosticListener, getCompilerOptions(), null, sources);
            SharedTypeAnnotationProcessor annotationProcessor = new SharedTypeAnnotationProcessor();
            annotationProcessor.setUserProps(properties);
            task.setProcessors(Collections.singleton(annotationProcessor));
            task.call();
        }
    }

    private List<File> walkAllSourceFiles() throws MojoExecutionException {
        SourceFileVisitor visitor = new SourceFileVisitor();
        try {
            for (String compileSourceRoot : project.getCompileSourceRoots()) {
                Files.walkFileTree(Paths.get(compileSourceRoot), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
        return visitor.getFiles();
    }

    private List<String> getCompilerOptions() throws MojoFailureException {
        List<String> options = new ArrayList<>(DEFAULT_COMPILER_OPTIONS.size() + 1);
        options.addAll(DEFAULT_COMPILER_OPTIONS);
        if (propertyFile != null) {
            if (Files.notExists(Paths.get(propertyFile))) {
                throw new MojoFailureException("Property file not found: " + propertyFile);
            }
            options.add("-Asharedtype.propsFile=" + propertyFile);
        }
        return options;
    }

    private static JavaCompiler getJavaCompiler() throws MojoExecutionException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            return compiler;
        }
        throw new MojoExecutionException("Java compiler not found, currently only compiler from jdk.compiler module is supported.");
    }

    private Charset getCharset() throws MojoFailureException {
        String encoding = project.getProperties().getProperty("project.build.sourceEncoding");
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (UnsupportedCharsetException e) {
                throw new MojoFailureException("Invalid 'encoding' option: " + encoding, e);
            }
        }
        return null;
    }
}

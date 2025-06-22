package online.sharedtype.maven;

import online.sharedtype.processor.SharedTypeAnnotationProcessor;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Mojo(name = "gen")
public final class SharedTypeGenMojo extends AbstractMojo {
    private static final List<String> DEFAULT_COMPILER_OPTIONS = Arrays.asList("-proc:only", "-Asharedtype.enabled=true");
    private @Inject RepositorySystem repositorySystem;
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding;
    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private String outputDirectory;
    @Parameter(defaultValue = "generated-sources", readonly = true)
    private String generatedSourcesDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JavaCompiler compiler = getJavaCompiler();
        DependencyResolver dependencyResolver = new DependencyResolver(repositorySystem, session, project);
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, charset(encoding));
        try {
//            fileManager.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, dependencyResolver.getProcessorDependencies());
            fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singleton(Paths.get(outputDirectory, generatedSourcesDirectory).toFile()));
            fileManager.setLocation(StandardLocation.CLASS_PATH, dependencyResolver.getSourceDependencies());
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
        Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromFiles(walkAllSourceFiles());
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, DEFAULT_COMPILER_OPTIONS, null, sources);
        task.setProcessors(Collections.singleton(new SharedTypeAnnotationProcessor()));
        task.call();
    }

    private List<File> walkAllSourceFiles() throws MojoExecutionException {
        SourceFileGatherer gatherer = new SourceFileGatherer();
        for (String compileSourceRoot : project.getCompileSourceRoots()) {
            try {
                Files.walkFileTree(Paths.get(compileSourceRoot), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, gatherer);
            } catch (IOException e) {
                throw new MojoExecutionException(e);
            }
        }
        return gatherer.getFiles();
    }

    private static JavaCompiler getJavaCompiler() throws MojoExecutionException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            return compiler;
        }
        throw new MojoExecutionException("Java compiler not found, currently only compiler from jdk.compiler module is supported.");
    }

    private static Charset charset(String encoding) throws MojoFailureException {
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

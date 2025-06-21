package online.sharedtype.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.util.ServiceLoader;

@Mojo(name = "gen")
public final class SharedTypeGenMojo extends AbstractMojo {
    private static final String COMPILER_ID = "javac";
    @Inject
    private MavenSession session;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenProject project = session.getCurrentProject();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    }

    private JavaCompiler getJavaCompiler() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            return compiler;
        }
        throw new IllegalStateException("Java compiler not found.");
    }
}

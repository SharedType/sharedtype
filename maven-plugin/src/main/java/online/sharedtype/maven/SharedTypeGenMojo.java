package online.sharedtype.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

@Mojo(name = "gen")
public final class SharedTypeGenMojo extends AbstractMojo {
    @Inject
    private MavenSession session;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    }
}

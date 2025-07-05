package online.sharedtype.maven;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cause Chung
 */
final class DependencyResolver {
    private final RepositorySystem repositorySystem;
    private final MavenSession session;
    private final MavenProject project;

    DependencyResolver(RepositorySystem repositorySystem, MavenSession session, MavenProject project) {
        this.repositorySystem = repositorySystem;
        this.session = session;
        this.project = project;
    }

    List<File> getClasspathDependencies() throws MojoExecutionException {
        try {
            ArtifactTypeRegistry artifactTypeRegistry =
                session.getRepositorySession().getArtifactTypeRegistry();
            CollectRequest collectRequest = new CollectRequest(
                project.getDependencies().stream().map(md -> RepositoryUtils.toDependency(md, artifactTypeRegistry)).collect(Collectors.toList()),
                project.getDependencyManagement().getDependencies().stream()
                    .map(md -> RepositoryUtils.toDependency(md, artifactTypeRegistry)).collect(Collectors.toList()),
                project.getRemoteProjectRepositories()
            );
            DependencyRequest dependencyRequest = new DependencyRequest();
            dependencyRequest.setCollectRequest(collectRequest);
            DependencyResult dependencyResult = repositorySystem.resolveDependencies(session.getRepositorySession(), dependencyRequest);
            return dependencyResult.getArtifactResults().stream()
                .map(resolved -> resolved.getArtifact().getFile())
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to resolve dependency, ", e);
        }
    }
}

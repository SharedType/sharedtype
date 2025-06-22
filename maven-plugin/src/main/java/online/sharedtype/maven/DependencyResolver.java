package online.sharedtype.maven;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

final class DependencyResolver {
    private static final String VERSION = DependencyResolver.class.getPackage().getImplementationVersion();
    private final RepositorySystem repositorySystem;
    private final MavenSession session;
    private final MavenProject project;

    DependencyResolver(RepositorySystem repositorySystem, MavenSession session, MavenProject project) {
        this.repositorySystem = repositorySystem;
        this.session = session;
        this.project = project;
    }

    List<File> getProcessorDependencies() throws MojoExecutionException {
        String artifactCoordinate = String.format("online.sharedtype:sharedtype-processor:%s", VERSION);
        try {
            Dependency dependency = new Dependency(new DefaultArtifact(artifactCoordinate), null);
            CollectRequest collectRequest =
                new CollectRequest(dependency, project.getRemoteProjectRepositories());
            DependencyRequest dependencyRequest = new DependencyRequest();
            dependencyRequest.setCollectRequest(collectRequest);
            DependencyResult dependencyResult = repositorySystem.resolveDependencies(session.getRepositorySession(), dependencyRequest);
            return dependencyResult.getArtifactResults().stream()
                .map(resolved -> resolved.getArtifact().getFile())
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MojoExecutionException(String.format("Failed to resolve '%s'", artifactCoordinate), e);
        }
    }

    List<File> getSourceDependencies() throws MojoExecutionException {
        try {
            ArtifactTypeRegistry artifactTypeRegistry =
                session.getRepositorySession().getArtifactTypeRegistry();
            CollectRequest collectRequest = new CollectRequest(
                project.getDependencies().stream().map(md -> convertDependency(md, artifactTypeRegistry)).collect(Collectors.toList()),
                project.getDependencyManagement().getDependencies().stream().map(md -> convertDependency(md, artifactTypeRegistry)).collect(Collectors.toList()),
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

    private static Dependency convertDependency(org.apache.maven.model.Dependency md, ArtifactTypeRegistry artifactTypeRegistry) {
        return RepositoryUtils.toDependency(md, artifactTypeRegistry);
    }
}

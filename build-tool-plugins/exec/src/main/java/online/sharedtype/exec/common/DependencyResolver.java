package online.sharedtype.exec.common;

import java.io.File;
import java.util.List;

/**
 * Adapter interface for retrieving classpath dependencies.
 * @author Cause Chung
 */
public interface DependencyResolver {
    List<File> getClasspathDependencies() throws Exception;
}

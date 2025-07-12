package online.sharedtype.exec.common;

import java.io.File;
import java.util.List;

public interface DependencyResolver {
    List<File> getClasspathDependencies() throws Exception;
}

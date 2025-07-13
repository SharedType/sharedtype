package online.sharedtype.gradle;

import java.io.File;
import java.util.Map;


/**
 * This will enable lazy configuration so that the actual values will only be resolved when they are needed
 * and can be reconfigured at any time during build configuration. See Gradle extension doc.
 *
 * @author Cause Chung
 */
final class SharedtypeConfigExtension {
    File outputDirectory;
    File propertyFile;
    Map<String, String> properties;
}

package online.sharedtype.gradle;

import java.io.File;
import java.util.Map;


/**
 * This will enable lazy configuration so that the actual values will only be resolved when they are needed
 * and can be reconfigured at any time during build configuration. See Gradle extension doc.
 *
 * @author Cause Chung
 */
public final class SharedtypeConfigExtension {
    private File outputDirectory;
    private File propertyFile;
    private Map<String, String> properties;
    private String sourceEncoding;

    public File getOutputDirectory() {
        return outputDirectory;
    }
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    public File getPropertyFile() {
        return propertyFile;
    }
    public void setPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    public String getSourceEncoding() {
        return sourceEncoding;
    }
    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }
}

package online.sharedtype.exec.common;

import online.sharedtype.processor.support.exception.SharedTypeException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SharedTypeApCompilerOptions {
    private static final List<String> DEFAULT_COMPILER_OPTIONS = Arrays.asList("-proc:only", "-Asharedtype.enabled=true");
    private static final String OPTION_PROPS_FILE_KEY = "-Asharedtype.propsFile=";
    private final String propertyFile;

    public SharedTypeApCompilerOptions(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public List<String> toList() {
        List<String> options = new ArrayList<>(DEFAULT_COMPILER_OPTIONS.size() + 1);
        options.addAll(DEFAULT_COMPILER_OPTIONS);
        if (propertyFile != null) {
            if (Files.notExists(Paths.get(propertyFile))) {
                throw new SharedTypeException("Property file not found: " + propertyFile);
            }
            options.add(OPTION_PROPS_FILE_KEY + propertyFile);
        }
        return options;
    }
}

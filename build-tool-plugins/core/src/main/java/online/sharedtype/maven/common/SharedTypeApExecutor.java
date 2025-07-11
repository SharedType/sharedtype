package online.sharedtype.maven.common;

import online.sharedtype.processor.SharedTypeAnnotationProcessor;
import online.sharedtype.processor.support.annotation.Nullable;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public final class SharedTypeApExecutor {
    private static final String JAVA8_VERSION = "1.8";
    private static final List<String> DEFAULT_COMPILER_OPTIONS = Arrays.asList("-proc:only", "-Asharedtype.enabled=true");



    private static List<File> walkAllSourceFiles(Iterable<String> compileSourceRoots) throws IOException {
        SourceFileVisitor visitor = new SourceFileVisitor();
        for (String compileSourceRoot : compileSourceRoots) {
            Files.walkFileTree(Paths.get(compileSourceRoot), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
        }
        return visitor.getFiles();
    }

    private static List<String> getCompilerOptions(@Nullable String propertyFile) {
        List<String> options = new ArrayList<>(DEFAULT_COMPILER_OPTIONS.size() + 1);
        options.addAll(DEFAULT_COMPILER_OPTIONS);
        if (propertyFile != null) {
            if (Files.notExists(Paths.get(propertyFile))) {
                throw new SharedTypeException("Property file not found: " + propertyFile);
            }
            options.add("-Asharedtype.propsFile=" + propertyFile);
        }
        return options;
    }

    private static JavaCompiler getJavaCompiler() throws SharedTypeException {
        String javaVersion = System.getProperty("java.specification.version");
        JavaCompiler compiler;
        if (JAVA8_VERSION.equals(javaVersion)) {
            try {
                Class<?> javacToolClass = SharedTypeAnnotationProcessor.class.getClassLoader().loadClass("com.sun.tools.javac.api.JavacTool");
                compiler = (JavaCompiler) javacToolClass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new SharedTypeException("Failed to load JavaCompiler.", e);
            }
        } else {
            compiler = ToolProvider.getSystemJavaCompiler();
        }
        if (compiler != null) {
            return compiler;
        }
        throw new SharedTypeException("Java compiler not found, currently only compiler from jdk.compiler module is supported.");
    }

    private static Charset getCharset(@Nullable String encoding) {
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (UnsupportedCharsetException e) {
                throw new SharedTypeException("Invalid 'encoding' option: " + encoding, e);
            }
        }
        return null;
    }
}

package online.sharedtype.exec.common;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Generic annotation processor executor. This module does not depend on sharedtype-ap.
 * @see SharedTypeApCompilerOptions
 * @author Cause Chung
 */
public final class AnnotationProcessorExecutor {
    private static final String JAVA8_VERSION = "1.8";
    private final Processor processor;
    private final Logger log;
    private final DependencyResolver dependencyResolver;

    public AnnotationProcessorExecutor(Processor processor, Logger log, DependencyResolver dependencyResolver) {
        this.processor = processor;
        this.log = log;
        this.dependencyResolver = dependencyResolver;
    }

    /** @return true if successful; false otherwise.  */
    public boolean execute(Path projectBaseDir,
                        Path outputDir,
                        Iterable<Path> compileSourceRoots,
                        String sourceEncoding,
                        Iterable<String> compilerOptions) throws Exception {
        SimpleDiagnosticListener diagnosticListener = new SimpleDiagnosticListener(log, projectBaseDir);
        JavaCompiler compiler = getJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, getCharset(sourceEncoding));
        try (SimpleLoggerWriter logger = new SimpleLoggerWriter(log)) {
            if (Files.notExists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singleton(outputDir.toFile()));
            fileManager.setLocation(StandardLocation.CLASS_PATH, dependencyResolver.getClasspathDependencies());
            Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjectsFromFiles(walkAllSourceFiles(compileSourceRoots));

            JavaCompiler.CompilationTask task = compiler.getTask(logger, fileManager, diagnosticListener, compilerOptions, null, sources);
            task.setProcessors(Collections.singleton(processor));
            return task.call();
        }
    }

    private static List<File> walkAllSourceFiles(Iterable<Path> compileSourceRoots) throws IOException {
        SourceFileVisitor visitor = new SourceFileVisitor();
        for (Path compileSourceRoot : compileSourceRoots) {
            if (Files.isDirectory(compileSourceRoot)) {
                Files.walkFileTree(compileSourceRoot, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
            }
        }
        return visitor.getFiles();
    }

    private static JavaCompiler getJavaCompiler() throws Exception {
        String javaVersion = System.getProperty("java.specification.version");
        JavaCompiler compiler;
        if (JAVA8_VERSION.equals(javaVersion)) {
            Class<?> javacToolClass = Class.forName("com.sun.tools.javac.api.JavacTool");
            compiler = (JavaCompiler) javacToolClass.getConstructor().newInstance();
        } else {
            compiler = ToolProvider.getSystemJavaCompiler();
        }
        if (compiler != null) {
            return compiler;
        }
        throw new ClassNotFoundException("Java compiler not found, currently only compiler from jdk.compiler module is supported.");
    }

    private static Charset getCharset(String encoding) {
        if (encoding != null) {
            return Charset.forName(encoding);
        }
        return null;
    }
}

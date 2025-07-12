package online.sharedtype.exec.common;

import online.sharedtype.processor.SharedTypeAnnotationProcessor;
import online.sharedtype.processor.support.annotation.Nullable;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

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

    public void execute(Path projectBaseDir,
                        Path outputDir,
                        Iterable<String> compileSourceRoots,
                        String sourceEncoding,
                        Collection<String> compilerOptions) throws Exception {
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
            task.call();
        }
    }

    private static List<File> walkAllSourceFiles(Iterable<String> compileSourceRoots) throws IOException {
        SourceFileVisitor visitor = new SourceFileVisitor();
        for (String compileSourceRoot : compileSourceRoots) {
            Files.walkFileTree(Paths.get(compileSourceRoot), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
        }
        return visitor.getFiles();
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

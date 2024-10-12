package org.jets.processor.context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

import lombok.Getter;

public final class Context {
    private final TypeCache resolvedTypes = new TypeCache();
    @Getter
    private final ProcessingEnvironment processingEnv;
    @Getter
    private final JetsProps props;

    public Context(ProcessingEnvironment processingEnv, JetsProps props) {
        this.processingEnv = processingEnv;
        this.props = props;
    }

    public void info(String message, Object... objects) {
        log(Diagnostic.Kind.NOTE, message, objects);
    }

    public void error(String message, Object... objects) {
        log(Diagnostic.Kind.ERROR, message, objects);
    }

    public void checkArgument(boolean condition, String message) {
        if (!condition) {
            log(Diagnostic.Kind.ERROR, message);
        }
    }

    public void saveType(String qualifiedName, String name) {
        resolvedTypes.add(qualifiedName, name);
    }

    public boolean hasType(String qualifiedName) {
        return resolvedTypes.contains(qualifiedName);
    }

    /**
     * Should check if the type is saved to the context by calling {@link #hasType(String)} first.
     * @return the simple name of the type, null if not saved to the context.
     */
    public String getSimpleName(String qualifiedName) {
        return resolvedTypes.getName(qualifiedName);
    }

    private void log(Diagnostic.Kind level, String message, Object... objects) {
        if (processingEnv != null) {
            processingEnv.getMessager().printMessage(level, String.format("[Jets] %s", String.format(message, objects)));
        }
    }
}

package org.jets.processor.context;

import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public final class GlobalContext {
    private static final String LINE_ENDING = System.lineSeparator();
    private final TypeCache typeCache = new TypeCache();
    @Getter
    private final ProcessingEnvironment processingEnv;
    @Getter
    private final JetsProps props;

    public GlobalContext(ProcessingEnvironment processingEnv, JetsProps props) {
        this.processingEnv = processingEnv;
        this.props = props;
    }

    public void info(String message) {
        log(Diagnostic.Kind.NOTE, message);
    }

    public void error(String message) {
        log(Diagnostic.Kind.ERROR, message);
    }

    public void checkArgument(boolean condition, String message) {
        if (!condition) {
            log(Diagnostic.Kind.ERROR, message);
        }
    }

    public void saveType(String qualifiedName, String name) {
        typeCache.add(qualifiedName, name);
    }

    public boolean hasType(String qualifiedName) {
        return typeCache.contains(qualifiedName);
    }

    /**
     * Should check if the type is saved to the context by calling {@link #hasType(String)} first.
     * @return the simple name of the type, null if not saved to the context.
     */
    public String getTypename(String qualifiedName) {
        return typeCache.getName(qualifiedName);
    }

    private void log(Diagnostic.Kind level, String message) {
        if (processingEnv != null) {
            processingEnv.getMessager().printMessage(level, String.format("[Jets] %s", message));
        }
    }
}

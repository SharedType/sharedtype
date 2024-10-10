package org.jets.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public record JetsContext(ProcessingEnvironment processingEnv) {
    public void info(String message) {
        log(Diagnostic.Kind.NOTE, message);
    }

    public void checkArgument(boolean condition, String message) {
        if (!condition) {
            log(Diagnostic.Kind.ERROR, message);
        }
    }

    private void log(Diagnostic.Kind level, String message) {
        if (processingEnv != null) {
            processingEnv.getMessager().printMessage(level, String.format("[Jets] %s", message));
        }
    }
}

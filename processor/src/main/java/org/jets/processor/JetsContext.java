package org.jets.processor;

import lombok.Builder;
import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

@Builder
@Getter
public final class JetsContext {
    private final ProcessingEnvironment processingEnv;

    public void log(Diagnostic.Kind level, String message) {
        if (processingEnv != null) {
            processingEnv.getMessager().printMessage(level, String.format("[Jets] %s", message));
        }
    }
}

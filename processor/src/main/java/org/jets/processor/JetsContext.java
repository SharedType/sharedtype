package org.jets.processor;

import lombok.Builder;
import lombok.Getter;
import org.jets.annotation.EmitTypescript;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

@Builder
@Getter
public final class JetsContext {
    private final ProcessingEnvironment processingEnv;
    private final EmitTypescript anno;

    public void log(Diagnostic.Kind level, String message) {
        if (processingEnv != null) {
            processingEnv.getMessager().printMessage(level, String.format("[Jets] %s", message));
        }
    }
}

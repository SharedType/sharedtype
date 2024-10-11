package org.jets.processor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

import lombok.Getter;
import org.jets.processor.config.JetsProps;
import org.jets.processor.domain.TypeSymbol;

public final class JetsContext {
    private final Set<TypeSymbol> typeSymbols = new HashSet<>();
    @Getter
    private final ProcessingEnvironment processingEnv;
    @Getter
    private final JetsProps props;

    public JetsContext(ProcessingEnvironment processingEnv, JetsProps props) {
        this.processingEnv = processingEnv;
        this.props = props;
    }

    public void info(String message) {
        log(Diagnostic.Kind.NOTE, message);
    }

    public void checkArgument(boolean condition, String message) {
        if (!condition) {
            log(Diagnostic.Kind.ERROR, message);
        }
    }

    public void saveTypeSymbol(TypeSymbol typeSymbol) {
        typeSymbols.add(typeSymbol);
    }

    public boolean hasTypeSymbol(TypeSymbol typeSymbol) {
        return typeSymbols.contains(typeSymbol);
    }

    private void log(Diagnostic.Kind level, String message) {
        if (processingEnv != null) {
            processingEnv.getMessager().printMessage(level, String.format("[Jets] %s", message));
        }
    }
}

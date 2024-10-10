package org.jets.processor;

import com.google.auto.service.AutoService;
import org.jets.annotation.EmitTypescript;
import org.jets.processor.parser.TypeElementParser;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("org.jets.annotation.EmitTypescript")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class JetsAnnotationProcessor extends AbstractProcessor {
    private final TypeElementParser parser;

    @Inject
    public JetsAnnotationProcessor(TypeElementParser parser) {
        this.parser = parser;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element instanceof TypeElement typeElement) {
                    var ctx = JetsContext.builder().processingEnv(processingEnv).build();
                    var typeInfo = parser.parse(typeElement, ctx);
                } else {
                    throw new UnsupportedOperationException("Unsupported element: " + element);
                }
            }
        }
        return true;
    }
}

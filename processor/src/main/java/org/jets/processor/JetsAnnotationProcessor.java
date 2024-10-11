package org.jets.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import org.jets.processor.context.GlobalContext;
import org.jets.processor.context.JetsProps;
import org.jets.processor.parser.TypeElementParser;

@SupportedAnnotationTypes("org.jets.annotation.EmitType")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class JetsAnnotationProcessor extends AbstractProcessor {
    private TypeElementParser parser;
    private GlobalContext ctx;

    @Override 
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        ctx = new GlobalContext(processingEnv, new JetsProps());
        var component = DaggerJetsComponent.builder().withContext(ctx).build();
        parser = component.parser();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element instanceof TypeElement typeElement) {
                    var typeInfoList = parser.parse(typeElement);
                    typeInfoList.forEach(t -> ctx.info(t.toString()));
                } else {
                    throw new UnsupportedOperationException("Unsupported element: " + element);
                }
            }
        }
        return true;
    }
}

package org.jets.processor;

import java.util.ArrayList;
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
import org.jets.processor.context.Constants;
import org.jets.processor.context.Context;
import org.jets.processor.context.JetsProps;
import org.jets.processor.domain.TypeDef;
import org.jets.processor.parser.TypeElementParser;
import org.jets.processor.resolver.TypeResolver;
import org.jets.processor.support.annotation.VisibleForTesting;
import org.jets.processor.support.exception.JetsInternalError;
import org.jets.processor.writer.TypeWriter;

@SupportedAnnotationTypes("org.jets.annotation.EmitType")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class JetsAnnotationProcessor extends AbstractProcessor {
    private static final boolean ANNOTATION_CONSUMED = true;
    private TypeElementParser parser;
    private TypeResolver resolver;
    private TypeWriter writer;
    private Context ctx;

    @Override 
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        ctx = new Context(processingEnv, new JetsProps()); // TODO: check thread safety
        var component = DaggerJetsComponent.builder().withContext(ctx).build();
        parser = component.parser();
        resolver = component.resolver();
        writer = component.writer();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return ANNOTATION_CONSUMED;
        }
        if (annotations.size() > 1) {
            throw new JetsInternalError(String.format("Only '%s' is expected.", Constants.ANNOTATION_QUALIFIED_NAME));
        }
        var annotation = annotations.iterator().next();
        ctx.checkArgument(annotation.getQualifiedName().contentEquals(Constants.ANNOTATION_QUALIFIED_NAME), null);

        doProcess(roundEnv.getElementsAnnotatedWith(annotation));
        return ANNOTATION_CONSUMED;
    }

    @VisibleForTesting
    void doProcess(Set<? extends Element> elements) {
        var defs = new ArrayList<TypeDef>();
        for (Element element : elements) {
            if (element instanceof TypeElement typeElement) {
                defs.addAll(parser.parse(typeElement));
            } else {
                throw new UnsupportedOperationException("Unsupported element: " + element);
            }
        }
        writer.write(resolver.resolve(defs));
    }
}

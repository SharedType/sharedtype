package org.sharedtype.processor;

import com.google.auto.service.AutoService;
import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.context.Props;
import org.sharedtype.domain.TypeDef;
import org.sharedtype.processor.parser.TypeDefParser;
import org.sharedtype.processor.resolver.TypeResolver;
import org.sharedtype.processor.support.annotation.VisibleForTesting;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;
import org.sharedtype.processor.writer.TypeWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.sharedtype.domain.Constants.ANNOTATION_QUALIFIED_NAME;
import static org.sharedtype.processor.support.Preconditions.checkArgument;

@SupportedAnnotationTypes("org.sharedtype.annotation.SharedType")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class AnnotationProcessorImpl extends AbstractProcessor {
    private static final boolean ANNOTATION_CONSUMED = true;
    private Context ctx;
    private TypeDefParser parser;
    private TypeResolver resolver;
    private TypeWriter writer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        ctx = new Context(processingEnv, new Props()); // TODO: check thread safety
        var component = DaggerComponents.builder().withContext(ctx).build();
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
            throw new SharedTypeInternalError(String.format("Only '%s' is expected.", ANNOTATION_QUALIFIED_NAME));
        }
        var annotation = annotations.iterator().next();
        checkArgument(annotation.getQualifiedName().contentEquals(ANNOTATION_QUALIFIED_NAME), "Wrong anno: %s", annotation);

        doProcess(roundEnv.getElementsAnnotatedWith(annotation));
        return ANNOTATION_CONSUMED;
    }

    @VisibleForTesting
    void doProcess(Set<? extends Element> elements) {
        var discoveredDefs = new ArrayList<TypeDef>(elements.size());
        for (Element element : elements) {
            if (element instanceof TypeElement typeElement) {
                var typeDef = parser.parse(typeElement);
                if (typeDef != null) {
                    discoveredDefs.add(typeDef);
                } else {
                    ctx.warning("Type '%s' is ignored, but annotated with '%s'.", typeElement.getQualifiedName(), ANNOTATION_QUALIFIED_NAME);
                }
            } else {
                throw new UnsupportedOperationException("Unsupported element: " + element);
            }
        }
        var resolvedDefs = resolver.resolve(discoveredDefs);
        try {
            writer.write(resolvedDefs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file,", e);
        }
    }
}

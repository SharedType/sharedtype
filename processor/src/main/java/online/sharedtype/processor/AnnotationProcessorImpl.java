package online.sharedtype.processor;

import com.google.auto.service.AutoService;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.PropsFactory;
import online.sharedtype.processor.parser.TypeDefParser;
import online.sharedtype.processor.resolver.TypeResolver;
import online.sharedtype.support.annotation.VisibleForTesting;
import online.sharedtype.support.exception.SharedTypeException;
import online.sharedtype.support.exception.SharedTypeInternalError;
import online.sharedtype.processor.writer.TypeWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static online.sharedtype.processor.domain.Constants.ANNOTATION_QUALIFIED_NAME;
import static online.sharedtype.support.Preconditions.checkArgument;

/**
 *
 * @author Cause Chung
 */
@SupportedAnnotationTypes("online.sharedtype.SharedType")
@SupportedOptions({"sharedtype.propsFile"})
@AutoService(Processor.class)
public final class AnnotationProcessorImpl extends AbstractProcessor {
    private static final String PROPS_FILE_OPTION_NAME = "sharedtype.propsFile";
    private static final String DEFAULT_USER_PROPS_FILE = "sharedtype.properties";
    private static final boolean ANNOTATION_CONSUMED = true;
    Context ctx;
    TypeDefParser parser;
    TypeResolver resolver;
    TypeWriter writer;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        String configFile = processingEnv.getOptions().getOrDefault(PROPS_FILE_OPTION_NAME, DEFAULT_USER_PROPS_FILE);
        ctx = new Context(processingEnv, PropsFactory.loadProps(Paths.get(configFile)));
        parser = TypeDefParser.create(ctx);
        resolver = TypeResolver.create(ctx, parser);
        writer = TypeWriter.create(ctx);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return ANNOTATION_CONSUMED;
        }
        if (annotations.size() > 1) {
            throw new SharedTypeInternalError(String.format("Only annotation %s is expected.", ANNOTATION_QUALIFIED_NAME));
        }
        TypeElement annotation = annotations.iterator().next();
        checkArgument(annotation.getQualifiedName().contentEquals(ANNOTATION_QUALIFIED_NAME), "Wrong anno: %s", annotation);

        doProcess(roundEnv.getElementsAnnotatedWith(annotation));
        return ANNOTATION_CONSUMED;
    }

    @VisibleForTesting
    void doProcess(Set<? extends Element> elements) {
        List<TypeDef> discoveredDefs = new ArrayList<>(elements.size());
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                TypeDef typeDef = parser.parse(typeElement);
                if (typeDef != null) {
                    discoveredDefs.add(typeDef);
                } else {
                    ctx.warning("Type '%s' is ignored or invalid, but annotated with '%s'.", typeElement.getQualifiedName().toString(), ANNOTATION_QUALIFIED_NAME);
                }
            } else {
                throw new SharedTypeInternalError(String.format("Unsupported element: %s of kind %s", element, element.getKind()));
            }
        }
        List<TypeDef> resolvedDefs = resolver.resolve(discoveredDefs);
        try {
            writer.write(resolvedDefs);
        } catch (IOException e) {
            throw new SharedTypeException("Failed to write,", e);
        }
    }
}

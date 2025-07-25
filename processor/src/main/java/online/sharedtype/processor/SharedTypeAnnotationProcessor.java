package online.sharedtype.processor;

import com.google.auto.service.AutoService;
import lombok.Setter;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.PropsFactory;
import online.sharedtype.processor.parser.TypeDefParser;
import online.sharedtype.processor.resolver.TypeResolver;
import online.sharedtype.processor.support.annotation.Nullable;
import online.sharedtype.processor.support.annotation.VisibleForTesting;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;
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
import java.util.Map;
import java.util.Set;

import static online.sharedtype.processor.domain.Constants.ANNOTATION_QUALIFIED_NAME;
import static online.sharedtype.processor.support.Preconditions.checkArgument;

/**
 *
 * @author Cause Chung
 */
@SupportedAnnotationTypes("online.sharedtype.SharedType")
@SupportedOptions({"sharedtype.propsFile", "sharedtype.enabled"})
@AutoService(Processor.class)
public final class SharedTypeAnnotationProcessor extends AbstractProcessor {
    private static final String PROPS_FILE_OPTION_NAME = "sharedtype.propsFile";
    private static final String PROPS_ENABLED = "sharedtype.enabled";
    private static final String DEFAULT_USER_PROPS_FILE = "sharedtype.properties";
    private static final boolean ANNOTATION_CONSUMED = true;
    /** Programmatically provided user properties, e.g. from Maven plugin */
    @Nullable @Setter
    private Map<String, String> userProps;
    private boolean enabled;
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
        String userPropsFile = processingEnv.getOptions().getOrDefault(PROPS_FILE_OPTION_NAME, DEFAULT_USER_PROPS_FILE);
        enabled = isEnabled(processingEnv);
        if (enabled) {
            ctx = new Context(processingEnv, PropsFactory.loadProps(Paths.get(userPropsFile), userProps));
            parser = TypeDefParser.create(ctx);
            resolver = TypeResolver.create(ctx, parser);
            writer = TypeWriter.create(ctx);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!enabled || annotations.isEmpty()) {
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
                List<TypeDef> typeDefs = parser.parse(typeElement);
                discoveredDefs.addAll(typeDefs);
                if (typeDefs.isEmpty()){
                    ctx.warn(element, "Type '%s' is ignored or invalid, but annotated with '%s'.",
                        typeElement.getQualifiedName().toString(), ANNOTATION_QUALIFIED_NAME);
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

    private static boolean isEnabled(ProcessingEnvironment processingEnv) {
        String enabledExpr = processingEnv.getOptions().getOrDefault(PROPS_ENABLED, "false");
        return enabledExpr.equalsIgnoreCase("true") || enabledExpr.equalsIgnoreCase("yes");
    }
}

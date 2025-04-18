package online.sharedtype.processor.context;

import com.sun.source.util.Trees;
import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Getter
public final class ContextMocks {
    private final TypeStore typeStore = spy(new TypeStore());
    private final RenderFlags renderFlags = spy(new RenderFlags());
    private final Props props;
    private final ProcessingEnvironment processingEnv = mock(ProcessingEnvironment.class);
    private final Types types = mock(Types.class);
    private final Elements elements = mock(Elements.class);
    private final Trees trees = mock(Trees.class);
    private final Context context = mock(Context.class);

    public ContextMocks() {
        this.props = spy(PropsFactory.loadProps(null));
        when(context.getProps()).thenReturn(props);
        when(context.getProcessingEnv()).thenReturn(processingEnv);
        when(processingEnv.getElementUtils()).thenReturn(elements);
        when(processingEnv.getTypeUtils()).thenReturn(types);
        when(context.getTypeStore()).thenReturn(typeStore);
        when(context.getRenderFlags()).thenReturn(renderFlags);
        when(context.getTrees()).thenReturn(trees);
    }

    public TypeElementMock typeElement(String qualifiedName) {
        return new TypeElementMock(qualifiedName, context);
    }

    public DeclaredTypeVariableElementMock declaredTypeVariable(String name, DeclaredType type) {
        return new DeclaredTypeVariableElementMock(name, type, context);
    }

    public PrimitiveVariableElementMock primitiveVariable(String name, TypeKind typeKind) {
        return new PrimitiveVariableElementMock(name, typeKind, context);
    }

    public <T extends TypeMirror> RecordComponentMock<T> recordComponent(String name, T type) {
        return new RecordComponentMock<>(name, type, context);
    }

    public TypeParameterElementMock typeParameter(String simpleName) {
        return new TypeParameterElementMock(simpleName, context);
    }

    public ExecutableElementMock executable(String name) {
        return new ExecutableElementMock(name, context);
    }

    public ArrayTypeMock array(TypeMirror componentType) {
        return new ArrayTypeMock(componentType);
    }

    public VariableTreeMock variableTree() {
        return new VariableTreeMock(context);
    }

    public NewClassTreeMock newClassTree() {
        return new NewClassTreeMock(context);
    }

    public LiteralTreeMock literalTree(Object value) {
        return new LiteralTreeMock(value, context);
    }

    public IdentifierTreeMock identifierTree(String name) {
        return new IdentifierTreeMock(name, context);
    }

    public AnnotationMirrorMock annotationMirror(DeclaredType declaredType) {
        return new AnnotationMirrorMock(declaredType);
    }
}

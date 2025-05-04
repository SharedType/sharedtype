package online.sharedtype.processor.parser.value;

import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.value.ValueHolder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ValueResolverBackendImplTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final ValueParser valueParser = mock(ValueParser.class);
    private final ValueResolverBackendImpl valueResolverBackend = new ValueResolverBackendImpl(valueParser);

    private final PackageElement packageElement = ctxMocks.packageElement("com.github.cuzfrog").element();
    private final TypeElement superTypeElement = ctxMocks.typeElement("com.github.cuzfrog.Super").element();
    private final TypeElement enclosingTypeElement = ctxMocks.typeElement("com.github.cuzfrog.Abc")
        .withPackageElement(packageElement).withSuperTypes(superTypeElement.asType()).element();
    private final VariableElement fieldElement = ctxMocks.primitiveVariable("field1", TypeKind.INT).element();
    private final ValueResolveContext.Builder valueResolveContextBuilder = ValueResolveContext.builder(ctxMocks.getContext())
        .enclosingTypeElement(enclosingTypeElement)
        .fieldElement(fieldElement);

    private final Scope scope = mock(Scope.class);
    private final TreePath treePath = mock(TreePath.class);

    @Test
    void getValueOfSimpleLiteralTree() {
        Tree tree = ctxMocks.literalTree(1).getTree();
        var valueResolveContext = valueResolveContextBuilder.tree(tree).build();
        assertThat(valueResolverBackend.recursivelyResolve(valueResolveContext)).isEqualTo(1);
    }

    @Test
    void getValueOfAssignmentTree() {
        Tree tree = ctxMocks.assignmentTree().withExpression(ctxMocks.literalTree(33).getTree()).getTree();
        var valueResolveContext = valueResolveContextBuilder.tree(tree).build();
        assertThat(valueResolverBackend.recursivelyResolve(valueResolveContext)).isEqualTo(33);
    }

    @Test
    void getValueFromReferenceToLocalElement() {
        VariableElement anotherFieldElement = ctxMocks.primitiveVariable("field2", TypeKind.INT)
            .withEnclosingElement(enclosingTypeElement).element();
        Tree treeOfAnotherField = ctxMocks.variableTree().withInitializer(ctxMocks.literalTree(55).getTree()).getTree();
        when(ctxMocks.getTrees().getTree(anotherFieldElement)).thenReturn(treeOfAnotherField);

        when(ctxMocks.getTrees().getPath(enclosingTypeElement)).thenReturn(treePath);
        when(ctxMocks.getTrees().getScope(treePath)).thenReturn(scope);
        try(var mockedUtils = Mockito.mockStatic(ValueResolveUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockedUtils.when(() -> ValueResolveUtils.findElementInLocalScope(scope, "field2", enclosingTypeElement)).thenReturn(anotherFieldElement);
            Tree tree = ctxMocks.variableTree().withInitializer(ctxMocks.identifierTree("field2").getTree()).getTree();
            var valueResolveContext = valueResolveContextBuilder.tree(tree).build();
            assertThat(valueResolverBackend.recursivelyResolve(valueResolveContext)).isEqualTo(55);
        }
    }

    @Test
    void getValueFromInheritedReference() {
        VariableElement fieldElementInSuper = ctxMocks.primitiveVariable("field2", TypeKind.INT)
            .withEnclosingElement(superTypeElement).element();
        Tree treeOfFieldInSuper = ctxMocks.variableTree().withInitializer(ctxMocks.literalTree(88).getTree()).getTree();
        when(ctxMocks.getTrees().getTree(fieldElementInSuper)).thenReturn(treeOfFieldInSuper);

        Tree tree = ctxMocks.variableTree().withInitializer(ctxMocks.identifierTree("field2").getTree()).getTree();
        var valueResolveContext = valueResolveContextBuilder.tree(tree).build();
        assertThat(valueResolverBackend.recursivelyResolve(valueResolveContext)).isEqualTo(88);
    }

    @Test
    void getValueFromReferenceToAnotherEnumConst() {
        var anotherEnumType = ctxMocks.typeElement("com.github.cuzfrog.AnotherEnum");
        VariableElement anotherEnumConstElement = ctxMocks.declaredTypeVariable("Value1", anotherEnumType.type())
            .withEnclosingElement(anotherEnumType.element())
            .withElementKind(ElementKind.ENUM_CONSTANT).element();
        var enumValue = mock(ValueHolder.class);
        when(valueParser.resolve(anotherEnumConstElement, anotherEnumType.element())).thenReturn(enumValue);

        when(ctxMocks.getTrees().getScope(any())).thenReturn(scope);
        try(var mockedUtils = Mockito.mockStatic(ValueResolveUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockedUtils.when(() -> ValueResolveUtils.findEnclosedElement(packageElement, "Value1")).thenReturn(anotherEnumConstElement);
            Tree tree = ctxMocks.variableTree().withInitializer(ctxMocks.identifierTree("Value1").getTree()).getTree();
            var valueResolveContext = valueResolveContextBuilder.tree(tree).build();
            assertThat(valueResolverBackend.recursivelyResolve(valueResolveContext)).isEqualTo(enumValue);
        }
    }

    @Test
    void getValueFromBigDecimalLiteral() {
        TypeElement bigDecimalTypeElement = ctxMocks.typeElement("java.math.BigDecimal").element();
        when(ctxMocks.getElements().getTypeElement("java.math.BigDecimal")).thenReturn(bigDecimalTypeElement);

        Tree tree = ctxMocks.variableTree().withInitializer(
            ctxMocks.newClassTree()
                .withIdentifier(ctxMocks.memberSelectTree("java.math.BigDecimal").getTree())
                .withArguments(ctxMocks.literalTree("123.456").getTree())
                .getTree()
        ).getTree();

        var valueResolveContext = valueResolveContextBuilder.tree(tree).build();
        assertThat(valueResolverBackend.recursivelyResolve(valueResolveContext)).isEqualTo("123.456");
    }
}

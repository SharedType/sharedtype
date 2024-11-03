package org.sharedtype.processor.context;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TypeElementMock extends AbstractElementMock<TypeElement, DeclaredType, TypeElementMock> {
    private static final ElementKind DEFAULT_ELEMENT_KIND = ElementKind.CLASS;
    TypeElementMock(String qualifiedName, Context ctx, Types types) {
        super(mock(TypeElement.class, qualifiedName), mock(DeclaredType.class, qualifiedName), ctx, types);
        when(element.getKind()).thenReturn(DEFAULT_ELEMENT_KIND);
        when(element.getNestingKind()).thenReturn(NestingKind.TOP_LEVEL);
        setQualifiedName(element, qualifiedName);
        setSimpleName(element, getLastPart(qualifiedName));
        when(type.getKind()).thenReturn(TypeKind.DECLARED);
        when(type.asElement()).thenReturn(element);
        when(types.asElement(type)).thenReturn(element);
    }

    public TypeElementMock withNestingKind(NestingKind nestingKind) {
        when(element.getNestingKind()).thenReturn(nestingKind);
        return this;
    }

    public TypeElementMock withModifiers(Modifier... modifiers) {
        when(element.getModifiers()).thenReturn(Set.of(modifiers));
        return this;
    }

    public TypeElementMock withEnclosedElements(Element... enclosedElements) {
        when(element.getEnclosedElements()).then(invoc -> Arrays.asList(enclosedElements));
        return this;
    }

    public TypeElementMock withTypeParameters(TypeParameterElement... typeParameters) {
        when(element.getTypeParameters()).then(invoc -> Arrays.asList(typeParameters));
        return this;
    }

    public TypeElementMock withSuperClass(DeclaredType superClass) {
        when(element.getSuperclass()).thenReturn(superClass);
        return this;
    }

    public TypeElementMock withInterfaces(DeclaredType... interfaces) {
        when(element.getInterfaces()).then(invoc -> Arrays.asList(interfaces));
        return this;
    }

    public TypeElementMock withRecordComponentElements(RecordComponentElement... recordComponentElements) {
        when(element.getRecordComponents()).then(invoc -> Arrays.asList(recordComponentElements));
        return this;
    }

    private static String getLastPart(String str) {
        int lastDotIndex = str.lastIndexOf('.');
        return str.substring(lastDotIndex + 1);
    }
}

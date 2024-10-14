package org.jets.processor.parser.context;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jets.processor.context.Context;
import org.jets.processor.context.ExtraUtils;
import org.jets.processor.context.JetsProps;
import org.junit.platform.commons.util.Preconditions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Getter
public final class ContextMocks {
    private final JetsProps props;
    private final ProcessingEnvironment processingEnv = mock(ProcessingEnvironment.class);
    private final Context context = mock(Context.class);
    private final ExtraUtils extraUtils = mock(ExtraUtils.class);
    private final Types types = mock(Types.class);
    private final Elements elements = mock(Elements.class);

    public ContextMocks(JetsProps props) {
        this.props = props;
        when(context.getProps()).thenReturn(props);
        when(context.getExtraUtils()).thenReturn(extraUtils);
        when(context.getProcessingEnv()).thenReturn(processingEnv);
        when(processingEnv.getElementUtils()).thenReturn(elements);
        when(processingEnv.getTypeUtils()).thenReturn(types);
    }

    public ContextMocks() {
        this(new JetsProps());
    }

    public <E extends Element, T extends TypeMirror> ElementAndTypeBuilder<E, T> typeMockBuilder(Class<E> elementClass, Class<T> typeMirrorClass) {
        return new ElementAndTypeBuilder<>(mock(elementClass), mock(typeMirrorClass));
    }
    public <E extends Element, T extends TypeMirror> ElementAndTypeBuilder<E, TypeMirror> typeMockBuilder(Class<E> elementClass) {
        return typeMockBuilder(elementClass, TypeMirror.class);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public final class ElementAndTypeBuilder<E extends Element, T extends TypeMirror> {
        private final E element;
        private final T type;
        private TypeKind typeKind;
        private String qualifiedName;
        private String typeElementQualifiedName;
        private List<String> typeArgumentQualifiedNames = Collections.emptyList();

        public ElementAndTypeBuilder<E, T> withTypeKind(TypeKind typeKind) {
            this.typeKind = typeKind;
            return this;
        }

        public ElementAndTypeBuilder<E, T> withQualifiedName(String qualifiedName) {
            Preconditions.condition(element instanceof TypeElement, "element must be a TypeElement");
            this.qualifiedName = qualifiedName;
            return this;
        }

        public ElementAndTypeBuilder<E, T> withTypeElementQualifiedName(String typeElementQualifiedName) {
            Preconditions.condition(type instanceof DeclaredType, "type must be a DeclaredType");
            this.typeElementQualifiedName = typeElementQualifiedName;
            return this;
        }

        public ElementAndTypeBuilder<E, T> withTypeArgumentQualifiedNames(String... typeArgumentQualifiedNames) {
            Preconditions.condition(type instanceof DeclaredType, "type must be a DeclaredType");
            this.typeArgumentQualifiedNames = Arrays.asList(typeArgumentQualifiedNames);
            return this;
        }

        public E build() {
            Objects.requireNonNull(typeKind, "typeKind must be set");
            when(element.asType()).thenReturn(type);
            if (element instanceof TypeElement typeElement) {
                Objects.requireNonNull(qualifiedName, "qualifiedName must be set");
                setQualifiedName(typeElement, qualifiedName);
            }
            if (type instanceof DeclaredType declaredType) {
                Objects.requireNonNull(typeElementQualifiedName, "typeElementQualifiedName must be set");
                var typeElement = mock(TypeElement.class);
                when(declaredType.asElement()).thenReturn(typeElement);
                setQualifiedName(typeElement, typeElementQualifiedName);

                var typeArgs = typeArgumentQualifiedNames.stream().map(qualifiedName -> {
                    var typeArgElement = mock(TypeElement.class);
                    setQualifiedName(typeArgElement, qualifiedName);
                    var typeArgMirror = mock(DeclaredType.class);
                    when(typeArgElement.asType()).thenReturn(typeArgMirror);
                    when(types.asElement(typeArgMirror)).thenReturn(typeArgElement);
                    return typeArgMirror;
                }).toList();
                when(extraUtils.getTypeArguments(declaredType)).thenReturn(typeArgs);
            }
            when(type.getKind()).thenReturn(typeKind);
            if (type instanceof DeclaredType || type instanceof TypeVariable) {
                when(types.asElement(type)).thenReturn(element);
            }
            return element;
        }
        
        private static void setQualifiedName(TypeElement typeElement, String qualifiedName) {
            var typeElementName = mock(Name.class);
            when(typeElement.getQualifiedName()).thenReturn(typeElementName);
            when(typeElementName.toString()).thenReturn(qualifiedName);
        }
    }
}

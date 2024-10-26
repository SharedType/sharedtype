package org.sharedtype.processor.resolver;

import org.sharedtype.domain.ArrayTypeInfo;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.EnumValueInfo;
import org.sharedtype.domain.FieldComponentInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.junit.jupiter.api.Test;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.parser.TypeDefParser;

import javax.lang.model.element.TypeElement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sharedtype.domain.Constants.STRING_TYPE_INFO;

final class LoopTypeResolverTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeDefParser typeDefParser = mock(TypeDefParser.class);
    private final LoopTypeResolver resolver = new LoopTypeResolver(ctxMocks.getContext(), typeDefParser);

    @Test
    void resolveFullClass() {
        var aTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.A")
            .resolved(false)
            .build();
        var typeDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Abc").simpleName("Abc")
            .components(List.of(
                FieldComponentInfo.builder().name("tuple").type(
                    ConcreteTypeInfo.builder()
                        .qualifiedName("com.github.cuzfrog.Tuple")
                        .resolved(false)
                        .typeArgs(List.of(
                            aTypeInfo,
                            new ArrayTypeInfo(ConcreteTypeInfo.builder()
                                .qualifiedName("com.github.cuzfrog.B")
                                .resolved(false)
                                .build())
                        ))
                        .build()
                ).build()
            ))
            .typeVariables(List.of(
                TypeVariableInfo.builder().name("T").build()
            ))
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.SuperClassA")
                    .resolved(false)
                    .build()
            ))
            .build();

        var tupleDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Tuple").simpleName("Tuple").build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.Tuple"))).thenReturn(tupleDef);
        var aDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.A").simpleName("A").build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.A"))).thenReturn(aDef);
        var bDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.B").simpleName("B").build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.B"))).thenReturn(bDef);
        var superADef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.SuperClassA").simpleName("SuperClassA")
            .components(List.of(
                FieldComponentInfo.builder().name("a").type(aTypeInfo).build()
            ))
            .build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.SuperClassA"))).thenReturn(superADef);

        var defs = resolver.resolve(List.of(typeDef));
        assertThat(defs).hasSize(5);
        {
            var a = (ClassDef) defs.get(0);
            assertThat(a).isSameAs(aDef);
        }
        {
            var b = (ClassDef) defs.get(1);
            assertThat(b).isSameAs(bDef);
        }
        {
            var tuple = (ClassDef) defs.get(2);
            assertThat(tuple).isSameAs(tupleDef);
        }
        {
            var superclassA = (ClassDef) defs.get(3);
            assertThat(superclassA.qualifiedName()).isEqualTo("com.github.cuzfrog.SuperClassA");
            assertThat(superclassA.simpleName()).isEqualTo("SuperClassA");
            assertThat(superclassA.components()).hasSize(1);
            FieldComponentInfo field = superclassA.components().get(0);
            assertThat(field.resolved()).isTrue();
            assertThat(field.name()).isEqualTo("a");
            ConcreteTypeInfo fieldType = (ConcreteTypeInfo) field.type();
            assertThat(fieldType.qualifiedName()).isEqualTo("com.github.cuzfrog.A");
        }
        {
            var abc = (ClassDef) defs.get(4);
            assertThat(abc).isSameAs(typeDef);
            FieldComponentInfo field = abc.components().get(0);
            assertThat(field.resolved()).isTrue();
            assertThat(field.name()).isEqualTo("tuple");
            ConcreteTypeInfo fieldType = (ConcreteTypeInfo) field.type();
            assertThat(fieldType.qualifiedName()).isEqualTo("com.github.cuzfrog.Tuple");
            assertThat(fieldType.typeArgs()).satisfiesExactly(
                a -> {
                    assertThat(a.resolved()).isTrue();
                    assertThat(((ConcreteTypeInfo) a).qualifiedName()).isEqualTo("com.github.cuzfrog.A");
                },
                bArr -> {
                    assertThat(bArr.resolved()).isTrue();
                    ConcreteTypeInfo arrComp = (ConcreteTypeInfo) ((ArrayTypeInfo) bArr).component();
                    assertThat(arrComp.resolved()).isTrue();
                    assertThat(arrComp.qualifiedName()).isEqualTo("com.github.cuzfrog.B");
                }
            );
        }
    }

    @Test
    void resolveSimpleEnum() {
        var typeDef = EnumDef.builder()
            .qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA")
            .enumValueInfos(List.of(
                new EnumValueInfo(STRING_TYPE_INFO, "Value1"),
                new EnumValueInfo(STRING_TYPE_INFO, "Value2")
            ))
            .build();

        var defs = resolver.resolve(List.of(typeDef));
        assertThat(defs).hasSize(1);
        var enumA = (EnumDef) defs.get(0);
        assertThat(enumA).isSameAs(typeDef);
    }

    private TypeElement mockElementByName(String qualifiedName) {
        var typeElement = ctxMocks.typeElement(qualifiedName).element();
        when(ctxMocks.getElements().getTypeElement(qualifiedName)).thenReturn(typeElement);
        return typeElement;
    }
}

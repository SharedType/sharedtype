package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.context.ContextMocks;
import org.junit.jupiter.api.Test;
import online.sharedtype.processor.parser.TypeDefParser;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;

final class LoopTypeResolverTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeDefParser typeDefParser = mock(TypeDefParser.class);
    private final LoopTypeResolver resolver = new LoopTypeResolver(ctxMocks.getContext(), typeDefParser);

    @Test
    void resolveFullClass() {
        TypeInfo aTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.A")
            .resolved(false)
            .build();
        TypeDef typeDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Abc").simpleName("Abc")
            .components(Collections.singletonList(
                FieldComponentInfo.builder().name("tuple").type(
                    ConcreteTypeInfo.builder()
                        .qualifiedName("com.github.cuzfrog.Tuple")
                        .resolved(false)
                        .typeArgs(Arrays.asList(
                            aTypeInfo,
                            new ArrayTypeInfo(ConcreteTypeInfo.builder()
                                .qualifiedName("com.github.cuzfrog.B")
                                .resolved(false)
                                .build())
                        ))
                        .build()
                ).build()
            ))
            .typeVariables(Collections.singletonList(
                TypeVariableInfo.builder().name("T").build()
            ))
            .supertypes(Collections.singletonList(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.SuperClassA")
                    .resolved(false)
                    .build()
            ))
            .build();

        ClassDef tupleDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.Tuple").simpleName("Tuple").build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.Tuple"))).thenReturn(tupleDef);
        ClassDef aDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.A").simpleName("A").build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.A"))).thenReturn(aDef);
        ClassDef bDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.B").simpleName("B").build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.B"))).thenReturn(bDef);
        ClassDef superADef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.SuperClassA").simpleName("SuperClassA")
            .components(Collections.singletonList(
                FieldComponentInfo.builder().name("a").type(aTypeInfo).build()
            ))
            .build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.SuperClassA"))).thenReturn(superADef);

        List<TypeDef> defs = resolver.resolve(Collections.singletonList(typeDef));
        assertThat(defs).hasSize(5);
        {
            ClassDef a = (ClassDef) defs.get(0);
            assertThat(a).isSameAs(aDef);
        }
        {
            ClassDef b = (ClassDef) defs.get(1);
            assertThat(b).isSameAs(bDef);
        }
        {
            ClassDef tuple = (ClassDef) defs.get(2);
            assertThat(tuple).isSameAs(tupleDef);
        }
        {
            ClassDef superclassA = (ClassDef) defs.get(3);
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
            ClassDef abc = (ClassDef) defs.get(4);
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
        EnumDef typeDef = EnumDef.builder()
            .qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA")
            .enumValueInfos(Arrays.asList(
                new EnumValueInfo(STRING_TYPE_INFO, "Value1"),
                new EnumValueInfo(STRING_TYPE_INFO, "Value2")
            ))
            .build();

        List<TypeDef> defs = resolver.resolve(Collections.singletonList(typeDef));
        assertThat(defs).hasSize(1);
        EnumDef enumA = (EnumDef) defs.get(0);
        assertThat(enumA).isSameAs(typeDef);
    }

    @Test
    void deduplicateTypeDef() {
        ClassDef classDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.ClassA").build();
        List<TypeDef> defs = resolver.resolve(List.of(classDef, classDef));
        assertThat(defs).hasSize(1);
        assertThat(defs.get(0)).isSameAs(classDef);
    }

    private TypeElement mockElementByName(String qualifiedName) {
        TypeElement typeElement = ctxMocks.typeElement(qualifiedName).element();
        when(ctxMocks.getElements().getTypeElement(qualifiedName)).thenReturn(typeElement);
        return typeElement;
    }
}

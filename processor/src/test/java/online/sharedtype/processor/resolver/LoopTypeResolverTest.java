package online.sharedtype.processor.resolver;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.component.FieldComponentInfo;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.MapTypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.TypeDefParser;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class LoopTypeResolverTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeDefParser typeDefParser = mock(TypeDefParser.class);
    private final LoopTypeResolver resolver = new LoopTypeResolver(ctxMocks.getContext(), typeDefParser);

    @Test
    void resolveFullClass() {
        ConcreteTypeInfo aTypeInfo = ConcreteTypeInfo.builder()
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
        when(typeDefParser.parse(ctxMocks.typeElement("com.github.cuzfrog.Tuple").element())).thenReturn(Collections.singletonList(tupleDef));
        ClassDef aDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.A").simpleName("A").build();
        when(typeDefParser.parse(ctxMocks.typeElement("com.github.cuzfrog.A").element())).thenReturn(Collections.singletonList(aDef));
        ClassDef bDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.B").simpleName("B").build();
        when(typeDefParser.parse(ctxMocks.typeElement("com.github.cuzfrog.B").element())).thenReturn(Collections.singletonList(bDef));
        ClassDef superADef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.SuperClassA").simpleName("SuperClassA")
            .components(Collections.singletonList(
                FieldComponentInfo.builder().name("a").type(aTypeInfo).build()
            ))
            .build();
        when(typeDefParser.parse(ctxMocks.typeElement("com.github.cuzfrog.SuperClassA").element())).thenReturn(Collections.singletonList(superADef));

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
        assertThat(aTypeInfo.resolved()).isTrue();
        assertThat(aTypeInfo.typeDef()).isEqualTo(aDef);
    }

    @Test
    void ignoredFieldShouldBeResolvedWithoutTypeDef() {
        ConcreteTypeInfo aTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.A")
            .resolved(false)
            .build();
        ClassDef typeDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Abc").simpleName("Abc")
            .components(Collections.singletonList(
                FieldComponentInfo.builder().name("a").type(aTypeInfo).build()
            ))
            .build();
        TypeElement typeElement = ctxMocks.typeElement("com.github.cuzfrog.A").element();
        when(ctxMocks.getContext().isIgnored(typeElement)).thenReturn(true);
        when(typeDefParser.parse(typeElement)).thenReturn(Collections.emptyList());

        List<TypeDef> defs = resolver.resolve(Collections.singletonList(typeDef));
        assertThat(defs).hasSize(1);
        ClassDef abc = (ClassDef) defs.get(0);
        assertThat(abc).isSameAs(typeDef);
        FieldComponentInfo field = abc.components().get(0);
        assertThat(field.resolved()).isTrue();
        assertThat(field.name()).isEqualTo("a");
        assertThat(field.type()).isEqualTo(aTypeInfo);
        assertThat(aTypeInfo.resolved()).isTrue();
        assertThat(aTypeInfo.typeDef()).isNull();
    }

    @Test
    void resolveSimpleEnum() {
        ConcreteTypeInfo enumType = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").build();
        EnumDef typeDef = EnumDef.builder()
            .qualifiedName("com.github.cuzfrog.EnumA").simpleName("EnumA")
            .enumValueInfos(Arrays.asList(
                EnumValueInfo.builder().name("Value1").value(ValueHolder.ofEnum(enumType, "Value1", enumType, "Value1")).build(),
                EnumValueInfo.builder().name("Value2").value(ValueHolder.ofEnum(enumType, "Value2", enumType, "Value2")).build()
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

    @Test
    void resolveMapType() {
        ConcreteTypeInfo mapValueType = ConcreteTypeInfo.builder().qualifiedName("a.b.MyObj").resolved(false).build();
        ClassDef classDef = ClassDef.builder().qualifiedName("com.github.cuzfrog.ClassA")
            .components(List.of(
                FieldComponentInfo.builder().name("mapField")
                    .type(MapTypeInfo.builder().keyType(Constants.STRING_TYPE_INFO).valueType(mapValueType).build())
                    .build()
            ))
            .build();
        var mapValueTypeDef = ClassDef.builder().qualifiedName("a.b.MyObj").build();
        when(typeDefParser.parse(ctxMocks.typeElement("a.b.MyObj").element())).thenReturn(Collections.singletonList(mapValueTypeDef));

        List<TypeDef> defs = resolver.resolve(List.of(classDef));
        assertThat(defs).hasSize(2);
        assertThat(defs.get(1)).isSameAs(classDef);
        assertThat(defs.get(0)).isSameAs(mapValueTypeDef);
        assertThat(mapValueType.resolved()).isTrue();
    }
}

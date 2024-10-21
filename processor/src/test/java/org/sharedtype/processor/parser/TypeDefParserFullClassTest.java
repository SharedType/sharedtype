package org.sharedtype.processor.parser;

import org.junit.jupiter.api.Test;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.context.TypeElementMock;
import org.sharedtype.processor.domain.ClassDef;
import org.sharedtype.processor.domain.ConcreteTypeInfo;
import org.sharedtype.processor.parser.type.TypeInfoParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TypeDefParserFullClassTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final TypeDefParserImpl parser = new TypeDefParserImpl(ctxMocks.getContext(), typeInfoParser);

    private final TypeElementMock string = ctxMocks.typeElement("java.lang.String");

    @Test
    void parseComplexClass() {
        var field1 = ctxMocks.primitiveVariable("field1", TypeKind.BOOLEAN);
        var field2 = ctxMocks.declaredTypeVariable("field2", string.type()).withElementKind(ElementKind.FIELD);
        var method1 = ctxMocks.executable("method1").withElementKind(ElementKind.METHOD);
        var method2 = ctxMocks.executable("getValue").withElementKind(ElementKind.METHOD);
        var element = ctxMocks.typeElement("com.github.cuzfrog.Abc")
          .withEnclosedElements(
            field1.element(),
            field2.element(),
            method1.element(),
            method2.element()
          )
          .withTypeParameters(
            ctxMocks.typeParameter("T").element(),
            ctxMocks.typeParameter("U").element()
          )
          .withSuperClass(
            ctxMocks.typeElement("com.github.cuzfrog.SuperClassA")
              .withEnclosedElements(
                ctxMocks.primitiveVariable("a", TypeKind.INT).element()
              )
              .type()
          )
          .withInterfaces(
            ctxMocks.typeElement("com.github.cuzfrog.InterfaceA").type(),
            ctxMocks.typeElement("com.github.cuzfrog.InterfaceB").type()
          )
          .element();

        var parsedField1Type = ConcreteTypeInfo.builder().qualifiedName("int").build();
        var parsedField2Type = ConcreteTypeInfo.builder().qualifiedName("java.lang.String").build();
        var parsedMethod2Type = ConcreteTypeInfo.builder().qualifiedName("int").build();
        when(typeInfoParser.parse(field1.type())).thenReturn(parsedField1Type);
        when(typeInfoParser.parse(field2.type())).thenReturn(parsedField2Type);
        when(typeInfoParser.parse(method2.type())).thenReturn(parsedMethod2Type);

        var defs = parser.parse(element);
        assertThat(defs).hasSize(1);
        var classDef = (ClassDef) defs.get(0);
        assertThat(classDef.name()).isEqualTo("Abc");

        // components
        assertThat(classDef.components()).hasSize(3);
        var field1Def = classDef.components().get(0);
        assertThat(field1Def.name()).isEqualTo("field1");
        assertThat(field1Def.type()).isEqualTo(parsedField1Type);
        var field2Def = classDef.components().get(1);
        assertThat(field2Def.name()).isEqualTo("field2");
        assertThat(field2Def.type()).isEqualTo(parsedField2Type);
        var method2Def = classDef.components().get(2);
        assertThat(method2Def.name()).isEqualTo("getValue");
        assertThat(method2Def.type()).isEqualTo(parsedMethod2Type);

        // type variables
        assertThat(classDef.typeVariables()).hasSize(2);
        var typeVar1 = classDef.typeVariables().get(0);
        assertThat(typeVar1.getName()).isEqualTo("T");
        var typeVar2 = classDef.typeVariables().get(1);
        assertThat(typeVar2.getName()).isEqualTo("U");

        // supertypes
        assertThat(classDef.supertypes()).hasSize(3);
        var supertype1 = (ClassDef)classDef.supertypes().get(0);
        assertThat(supertype1.qualifiedName()).isEqualTo("com.github.cuzfrog.SuperClassA");
        assertThat(supertype1.components()).hasSize(1);
        var supertype1Field = supertype1.components().get(0);
        assertThat(supertype1Field.name()).isEqualTo("a");
        var supertype2 = classDef.supertypes().get(1);
        assertThat(supertype2.qualifiedName()).isEqualTo("com.github.cuzfrog.InterfaceA");
        var supertype3 = classDef.supertypes().get(2);
        assertThat(supertype3.qualifiedName()).isEqualTo("com.github.cuzfrog.InterfaceB");
    }
}

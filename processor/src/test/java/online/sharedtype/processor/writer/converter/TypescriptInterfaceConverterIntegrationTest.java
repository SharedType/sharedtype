package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import online.sharedtype.processor.writer.render.Template;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;

final class TypescriptInterfaceConverterIntegrationTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptInterfaceConverter converter = new TypescriptInterfaceConverter(
        ctxMocks.getContext(), TypeExpressionConverter.typescript(ctxMocks.getContext()));

    @Test
    void skipMapClassDef() {
        ClassDef classDef = ClassDef.builder()
            .build();
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().mapType(true).build());
        assertThat(converter.shouldAccept(classDef)).isFalse();
    }

    @Test
    void writeInterface() {
        ClassDef classDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.ClassA")
            .simpleName("ClassA")
            .typeVariables(Arrays.asList(
                TypeVariableInfo.builder().name("T").build(),
                TypeVariableInfo.builder().name("U").build()
            ))
            .supertypes(Collections.singletonList(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.SuperClassA")
                    .simpleName("SuperClassA")
                    .typeArgs(Collections.singletonList(TypeVariableInfo.builder().name("U").build()))
                    .build()
            ))
            .components(Arrays.asList(
                FieldComponentInfo.builder().name("field1").type(INT_TYPE_INFO).optional(true).build(),
                FieldComponentInfo.builder().name("field2").type(STRING_TYPE_INFO).optional(false).build(),
                FieldComponentInfo.builder().name("field3")
                    .type(
                        new ArrayTypeInfo(
                            new ArrayTypeInfo(
                                ConcreteTypeInfo.builder()
                                    .qualifiedName("com.github.cuzfrog.Container")
                                    .simpleName("Container")
                                    .typeArgs(Collections.singletonList(TypeVariableInfo.builder().name("T").build()))
                                    .build()
                            )
                        )
                    )
                    .build(),
                FieldComponentInfo.builder().name("field4")
                    .type(new ArrayTypeInfo(TypeVariableInfo.builder().name("T").build()))
                    .build(),
                FieldComponentInfo.builder().name("mapField")
                    .type(
                        ConcreteTypeInfo.builder()
                            .qualifiedName("java.util.Map")
                            .simpleName("Map")
                            .mapType(true)
                            .typeArgs(Arrays.asList(STRING_TYPE_INFO, INT_TYPE_INFO))
                            .build()
                    )
                    .build()
            ))
            .build();
        var tuple = converter.convert(classDef);
        assertThat(tuple).isNotNull();
        assertThat(tuple.a()).isEqualTo(Template.TEMPLATE_TYPESCRIPT_INTERFACE);
        TypescriptInterfaceConverter.InterfaceExpr model = (TypescriptInterfaceConverter.InterfaceExpr) tuple.b();
        assertThat(model.name).isEqualTo("ClassA");
        assertThat(model.typeParameters).containsExactly("T", "U");
        assertThat(model.supertypes).containsExactly("SuperClassA<U>");
        assertThat(model.properties).hasSize(5);
        TypescriptInterfaceConverter.PropertyExpr prop1 = model.properties.get(0);
        assertThat(prop1.name).isEqualTo("field1");
        assertThat(prop1.type).isEqualTo("number");
        assertThat(prop1.optional).isTrue();

        TypescriptInterfaceConverter.PropertyExpr prop2 = model.properties.get(1);
        assertThat(prop2.name).isEqualTo("field2");
        assertThat(prop2.type).isEqualTo("string");
        assertThat(prop2.optional).isFalse();

        TypescriptInterfaceConverter.PropertyExpr prop3 = model.properties.get(2);
        assertThat(prop3.name).isEqualTo("field3");
        assertThat(prop3.type).isEqualTo("Container<T>[][]");
        assertThat(prop3.optional).isFalse();

        TypescriptInterfaceConverter.PropertyExpr prop4 = model.properties.get(3);
        assertThat(prop4.name).isEqualTo("field4");
        assertThat(prop4.type).isEqualTo("T[]");
        assertThat(prop4.optional).isFalse();

        TypescriptInterfaceConverter.PropertyExpr prop5 = model.properties.get(4);
        assertThat(prop5.name).isEqualTo("mapField");
        assertThat(prop5.type).isEqualTo("{ [key: string]: number }");
    }
}

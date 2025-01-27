package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class RustStructConverterIntegrationTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustStructConverter converter = new RustStructConverter(ctxMocks.getContext(), TypeExpressionConverter.rust(ctxMocks.getContext()));

    @Test
    void skipNonClassDef() {
        assertThat(converter.shouldAccept(EnumDef.builder().build())).isFalse();
    }

    @Test
    void shouldAcceptClassDefAnnotated() {
        assertThat(converter.shouldAccept(ClassDef.builder().build())).isFalse();
        assertThat(converter.shouldAccept(ClassDef.builder().annotated(true).build())).isTrue();
        assertThat(converter.shouldAccept(ClassDef.builder().referencedByAnnotated(true).build())).isTrue();
    }


    @Test
    void convert() {
        ConcreteTypeInfo recursiveTypeInfo = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.RecursiveClass")
            .simpleName("RecursiveClass")
            .build();
        ClassDef recursiveTypeDef = ClassDef.builder()
            .simpleName("RecursiveClass")
            .qualifiedName("com.github.cuzfrog.RecursiveClass")
            .components(List.of(
                FieldComponentInfo.builder()
                    .name("recursiveRef")
                    .type(recursiveTypeInfo)
                    .build()
            ))
            .cyclicReferenced(true)
            .build();
        recursiveTypeInfo.markShallowResolved(recursiveTypeDef);

        ClassDef classDef = ClassDef.builder()
            .simpleName("ClassA")
            .qualifiedName("com.github.cuzfrog.ClassA")
            .typeVariables(List.of(
                TypeVariableInfo.builder().name("T").contextTypeQualifiedName("com.github.cuzfrog.ClassA").build()
            ))
            .components(List.of(
                FieldComponentInfo.builder()
                    .name("field1")
                    .type(Constants.INT_TYPE_INFO)
                    .build(),
                FieldComponentInfo.builder()
                    .name("field2")
                    .type(TypeVariableInfo.builder().name("T").contextTypeQualifiedName("com.github.cuzfrog.ClassA").build())
                    .build(),
                FieldComponentInfo.builder()
                    .name("field3")
                    .type(recursiveTypeInfo)
                    .build()
            ))
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.SuperClassA")
                    .simpleName("SuperClassA")
                    .typeArgs(List.of(
                        Constants.STRING_TYPE_INFO
                    ))
                    .typeDef(
                        ClassDef.builder()
                            .simpleName("SuperClassA")
                            .qualifiedName("com.github.cuzfrog.SuperClassA")
                            .typeVariables(List.of(
                                TypeVariableInfo.builder().name("T").contextTypeQualifiedName("com.github.cuzfrog.SuperClassA").build()
                            ))
                            .components(List.of(
                                FieldComponentInfo.builder()
                                    .name("superField1")
                                    .type(TypeVariableInfo.builder().name("T").contextTypeQualifiedName("com.github.cuzfrog.SuperClassA").build())
                                    .build()
                            ))
                            .build()
                    )
                    .build()
            ))
            .build();
        var data = converter.convert(classDef);
        assertThat(data).isNotNull();
        var model = (RustStructConverter.StructExpr) data.b();
        assertThat(model.name).isEqualTo("ClassA");
        assertThat(model.typeParameters).containsExactly("T");

        assertThat(model.properties).hasSize(4);
        RustStructConverter.PropertyExpr prop1 = model.properties.get(0);
        assertThat(prop1.name).isEqualTo("field1");
        assertThat(prop1.type).isEqualTo("i32");
        assertThat(prop1.optional).isFalse();
        assertThat(prop1.typeExpr()).isEqualTo("i32");

        RustStructConverter.PropertyExpr prop2 = model.properties.get(1);
        assertThat(prop2.name).isEqualTo("field2");
        assertThat(prop2.type).isEqualTo("T");
        assertThat(prop2.optional).isFalse();

        RustStructConverter.PropertyExpr prop3 = model.properties.get(2);
        assertThat(prop3.name).isEqualTo("field3");
        assertThat(prop3.type).isEqualTo("Box<RecursiveClass>");
        assertThat(prop3.optional).isTrue();
        assertThat(prop3.typeExpr()).isEqualTo("Option<Box<RecursiveClass>>");

        RustStructConverter.PropertyExpr prop4 = model.properties.get(3);
        assertThat(prop4.name).isEqualTo("superField1");
        assertThat(prop4.type).isEqualTo("String");
        assertThat(prop4.optional).isFalse();
        assertThat(prop4.typeExpr()).isEqualTo("String");
    }
}

package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.component.FieldComponentInfo;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class GoStructConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeExpressionConverter typeExpressionConverter = TypeExpressionConverter.go(ctxMocks.getContext());
    private final GoStructConverter converter = new GoStructConverter(typeExpressionConverter);

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
                    .build(),
                FieldComponentInfo.builder()
                    .name("mapField")
                    .type(ConcreteTypeInfo.builder()
                        .qualifiedName("java.util.Map")
                        .simpleName("Map")
                        .kind(ConcreteTypeInfo.Kind.MAP)
                        .typeArgs(List.of(
                            Constants.STRING_TYPE_INFO,
                            Constants.INT_TYPE_INFO
                        ))
                        .build()
                    )
                    .build()
            ))
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.SuperClassA")
                    .simpleName("SuperClassA")
                    .typeArgs(List.of(
                        Constants.STRING_TYPE_INFO
                    ))
                    .build()
            ))
            .build();

        var data = converter.convert(classDef);
        assertThat(data).isNotNull();
        var model = (GoStructConverter.StructExpr) data.b();
        assertThat(model.name).isEqualTo("ClassA");
        assertThat(model.typeParameters).containsExactly("T");
        assertThat(model.typeParametersExpr()).isEqualTo("[T any]");
        assertThat(model.supertypes).containsExactly("SuperClassA[string]");

        assertThat(model.properties).hasSize(4);
        GoStructConverter.PropertyExpr prop1 = model.properties.get(0);
        assertThat(prop1.name).isEqualTo("Field1");
        assertThat(prop1.type).isEqualTo("int32");
        assertThat(prop1.typeExpr()).isEqualTo("int32");

        GoStructConverter.PropertyExpr prop2 = model.properties.get(1);
        assertThat(prop2.name).isEqualTo("Field2");
        assertThat(prop2.type).isEqualTo("T");

        GoStructConverter.PropertyExpr prop3 = model.properties.get(2);
        assertThat(prop3.name).isEqualTo("Field3");
        assertThat(prop3.type).isEqualTo("RecursiveClass");
        assertThat(prop3.typeExpr()).isEqualTo("*RecursiveClass");

        GoStructConverter.PropertyExpr prop5 = model.properties.get(3);
        assertThat(prop5.name).isEqualTo("MapField");
        assertThat(prop5.type).isEqualTo("map[string]int32");
    }
}

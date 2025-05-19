package online.sharedtype.processor.context;

import online.sharedtype.SharedType;
import org.junit.jupiter.api.Test;
import online.sharedtype.processor.support.exception.SharedTypeException;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

final class PropsFactoryTest {
    @Test
    void loadUserProps() {
        Props props = PropsFactory.loadProps(resolveResource("test-sharedtype-user.properties"));
        assertThat(props.getTargets()).containsExactly(OutputTarget.CONSOLE, OutputTarget.TYPESCRIPT);
        assertThat(props.getOptionalAnnotations()).containsExactly("a.b.TsOptional");
        assertThat(props.getTypescript().getJavaObjectMapType()).isEqualTo("unknown");
    }

    @Test
    void loadDefaultProps() {
        Props props = PropsFactory.loadProps(Paths.get("not-exist"));
        assertThat(props.getTargets()).containsExactly(OutputTarget.TYPESCRIPT);
        assertThat(props.getTargetTypes()).containsExactly(SharedType.TargetType.TYPESCRIPT);
        assertThat(props.getOptionalAnnotations()).containsExactly("javax.annotation.Nullable");
        assertThat(props.getIgnoreAnnotations()).isEmpty();
        assertThat(props.getAccessorAnnotations()).isEmpty();
        assertThat(props.getEnumValueAnnotations()).isEmpty();
        assertThat(props.getOptionalContainerTypes()).containsExactly("java.util.Optional");
        assertThat(props.getAccessorGetterPrefixes()).containsExactly("get", "is");
        assertThat(props.getArraylikeTypeQualifiedNames()).containsExactly("java.lang.Iterable");
        assertThat(props.getMaplikeTypeQualifiedNames()).containsExactly("java.util.Map");
        assertThat(props.getDatetimelikeTypeQualifiedNames()).containsExactly(
            "java.util.Date", "java.time.temporal.Temporal", "org.joda.time.base.AbstractInstant", "org.joda.time.base.AbstractPartial");
        assertThat(props.getIgnoredTypeQualifiedNames()).containsExactlyInAnyOrder(
            "java.lang.Object",
            "java.lang.Enum",
            "java.io.Serializable",
            "java.lang.Record",
            "java.lang.Cloneable",
            "java.lang.Comparable"
        );
        assertThat(props.getIgnoredFieldNames()).containsExactly("serialVersionUID");
        assertThat(props.isConstantNamespaced()).isTrue();

        Props.Typescript typescriptProps = props.getTypescript();
        assertThat(typescriptProps.getOutputFileName()).isEqualTo("types.ts");
        assertThat(typescriptProps.getInterfacePropertyDelimiter()).isEqualTo(';');
        assertThat(typescriptProps.getJavaObjectMapType()).isEqualTo("any");
        assertThat(typescriptProps.getTargetDatetimeTypeLiteral()).isEqualTo("string");
        assertThat(typescriptProps.getOptionalFieldFormats()).containsExactly(Props.Typescript.OptionalFieldFormat.QUESTION_MARK);
        assertThat(typescriptProps.getEnumFormat()).isEqualTo(Props.Typescript.EnumFormat.UNION);
        assertThat(typescriptProps.getFieldReadonlyType()).isEqualTo(Props.Typescript.FieldReadonlyType.ACYCLIC);
        assertThat(typescriptProps.getTypeMappings()).isEmpty();
        assertThat(typescriptProps.getCustomCodePath()).isEqualTo("sharedtype-custom-code.ts");

        Props.Go goProps = props.getGo();
        assertThat(goProps.getOutputFileName()).isEqualTo("types.go");
        assertThat(goProps.getOutputFilePackageName()).isEqualTo("sharedtype");
        assertThat(goProps.getJavaObjectMapType()).isEqualTo("any");
        assertThat(goProps.getTargetDatetimeTypeLiteral()).isEqualTo("string");
        assertThat(goProps.getEnumFormat()).isEqualTo(Props.Go.EnumFormat.CONST);
        assertThat(goProps.getTypeMappings()).isEmpty();
        assertThat(goProps.getCustomCodePath()).isEqualTo("sharedtype-custom-code.go");

        Props.Rust rustProps = props.getRust();
        assertThat(rustProps.getOutputFileName()).isEqualTo("types.rs");
        assertThat(rustProps.isAllowDeadcode()).isEqualTo(true);
        assertThat(rustProps.isConvertToSnakeCase()).isEqualTo(false);
        assertThat(rustProps.getDefaultTypeMacros()).containsExactly("Debug");
        assertThat(rustProps.getTargetDatetimeTypeLiteral()).isEqualTo("String");
        assertThat(rustProps.getTypeMappings()).isEmpty();
        assertThat(rustProps.getCustomCodePath()).isEqualTo("sharedtype-custom-code.rs");
    }

    @Test
    void wrongTarget() {
        assertThatThrownBy(() -> PropsFactory.loadProps(resolveResource("test-sharedtype-wrong-target.properties")))
            .isInstanceOf(SharedTypeException.class);
    }

    @Test
    void wrongTypescriptOptionalFieldFormat() {
        assertThatThrownBy(() -> PropsFactory.loadProps(resolveResource("test-sharedtype-wrong-ts-optional-field-format.properties")))
            .isInstanceOf(SharedTypeException.class)
            .cause().cause()
            .hasMessageContaining("Unknown optional field format: 'abc', only '?', 'null', 'undefined' are allowed");
    }

    @Test
    void typeMappings() {
        Props props = PropsFactory.loadProps(resolveResource("test-sharedtype-type-mappings.properties"));
        assertThat(props.getTypescript().getTypeMappings()).containsExactly(
            entry("MyType1", "RenamedType1"),
            entry("MyType2", "RenamedType2")
        );
    }

    private static Path resolveResource(String resource) {
        try {
            return Paths.get(PropsFactoryTest.class.getClassLoader().getResource(resource).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

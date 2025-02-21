package online.sharedtype.processor.context;

import org.junit.jupiter.api.Test;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class PropsFactoryTest {
    @Test
    void loadUserProps() {
        Props props = PropsFactory.loadProps(resolveResource("test-sharedtype-user.properties"));
        assertThat(props.getTargets()).containsExactly(OutputTarget.CONSOLE, OutputTarget.TYPESCRIPT);
        assertThat(props.getOptionalAnnotations()).containsExactly(Override.class);
        assertThat(props.getTypescript().getJavaObjectMapType()).isEqualTo("unknown");
    }

    @Test
    void loadDefaultProps() {
        Props props = PropsFactory.loadProps(Paths.get("not-exist"));
        assertThat(props.getTargets()).containsExactly(OutputTarget.TYPESCRIPT);
        assertThat(props.getOptionalAnnotations()).containsExactly(Nullable.class);
        assertThat(props.getOptionalContainerTypes()).containsExactly(java.util.Optional.class);
        assertThat(props.getAccessorGetterPrefixes()).containsExactly("get", "is");
        assertThat(props.getArraylikeTypeQualifiedNames()).containsExactly("java.lang.Iterable");
        assertThat(props.getMaplikeTypeQualifiedNames()).containsExactly("java.util.Map");
        assertThat(props.getIgnoredTypeQualifiedNames()).containsExactlyInAnyOrder(
            "java.lang.Object",
            "java.lang.Enum",
            "java.io.Serializable",
            "java.lang.Record",
            "java.lang.Cloneable"
        );
        assertThat(props.getIgnoredFieldNames()).containsExactly("serialVersionUID");
        assertThat(props.isConstantNamespaced()).isTrue();

        Props.Typescript typescriptProps = props.getTypescript();
        assertThat(typescriptProps.getOutputFileName()).isEqualTo("types.ts");
        assertThat(typescriptProps.getInterfacePropertyDelimiter()).isEqualTo(';');
        assertThat(typescriptProps.getJavaObjectMapType()).isEqualTo("any");

        Props.Rust rustProps = props.getRust();
        assertThat(rustProps.getOutputFileName()).isEqualTo("types.rs");
        assertThat(rustProps.isAllowDeadcode()).isEqualTo(true);
        assertThat(rustProps.isConvertToSnakeCase()).isEqualTo(false);
        assertThat(rustProps.getDefaultTypeMacros()).containsExactly("Debug");
    }

    @Test
    void wrongTarget() {
        assertThatThrownBy(() -> PropsFactory.loadProps(resolveResource("test-sharedtype-wrong-target.properties")))
            .isInstanceOf(SharedTypeException.class);
    }

    private static Path resolveResource(String resource) {
        try {
            return Paths.get(PropsFactoryTest.class.getClassLoader().getResource(resource).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

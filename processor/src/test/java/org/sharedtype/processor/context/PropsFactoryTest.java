package org.sharedtype.processor.context;

import org.junit.jupiter.api.Test;
import org.sharedtype.processor.support.exception.SharedTypeException;

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
        assertThat(props.getTargets()).containsExactly(OutputTarget.TYPESCRIPT, OutputTarget.CONSOLE);
        assertThat(props.getOptionalAnno()).isEqualTo(Override.class);
        assertThat(props.getTypescript().getJavaObjectMapType()).isEqualTo("unknown");
    }

    @Test
    void loadDefaultProps() {
        Props props = PropsFactory.loadProps(Paths.get("not-exist"));
        assertThat(props.getTargets()).containsExactly(OutputTarget.TYPESCRIPT);
        assertThat(props.getOptionalAnno()).isEqualTo(Nullable.class);
        assertThat(props.getAccessorGetterPrefixes()).containsExactly("get", "is");
        assertThat(props.getArraylikeTypeQualifiedNames()).containsExactly("java.lang.Iterable");
        assertThat(props.getMaplikeTypeQualifiedNames()).containsExactly("java.util.Map");
        assertThat(props.getIgnoredTypeQualifiedNames()).containsExactlyInAnyOrder(
            "java.lang.Object",
            "java.lang.Enum",
            "java.io.Serializable",
            "java.lang.Record"
        );

        Props.Typescript typescriptProps = props.getTypescript();
        assertThat(typescriptProps.getOutputFileName()).isEqualTo("types.d.ts");
        assertThat(typescriptProps.getInterfacePropertyDelimiter()).isEqualTo(';');
        assertThat(typescriptProps.getJavaObjectMapType()).isEqualTo("any");
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

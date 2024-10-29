package org.sharedtype.processor.context;

import org.junit.jupiter.api.Test;
import org.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class PropsFactoryTest {

    @Test
    void loadUserProps() {
        var props = PropsFactory.loadProps("test-sharedtype-user.properties");
        assertThat(props.getTargets()).containsExactly(OutputTarget.TYPESCRIPT, OutputTarget.CONSOLE);
        assertThat(props.getOptionalAnno()).isEqualTo(Override.class);
        assertThat(props.getTypescript().getJavaObjectMapType()).isEqualTo("unknown");
    }

    @Test
    void loadDefaultProps() {
        var props = PropsFactory.loadProps("not-exist.properties");
        assertThat(props.getTargets()).containsExactly(OutputTarget.TYPESCRIPT);
        assertThat(props.getOptionalAnno()).isEqualTo(Nullable.class);
        assertThat(props.getAccessorGetterPrefixes()).containsExactly("get", "is");
        assertThat(props.getArraylikeTypeQualifiedNames()).containsExactly("java.lang.Iterable");
        assertThat(props.getMaplikeTypeQualifiedNames()).containsExactly("java.util.Map");
        assertThat(props.getIgnoredTypeQualifiedNames()).containsExactlyInAnyOrder(
            "java.lang.Object",
            "java.lang.Enum",
            "java.io.Serializable",
            "java.util.Record"
        );

        var typescriptProps = props.getTypescript();
        assertThat(typescriptProps.getOutputFileName()).isEqualTo("types.d.ts");
        assertThat(typescriptProps.getInterfacePropertyDelimiter()).isEqualTo(';');
        assertThat(typescriptProps.getJavaObjectMapType()).isEqualTo("any");
    }

    @Test
    void wrongTarget() {
        assertThatThrownBy(() -> PropsFactory.loadProps("test-sharedtype-wrong-target.properties"))
            .isInstanceOf(SharedTypeException.class);
    }
}

package online.sharedtype.e2e;

import online.sharedtype.it.java8.EnumSize;
import online.sharedtype.it.java8.JavaClass;
import online.sharedtype.processor.domain.TargetCodeType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test is to check e2e equality to ensure the serialization/deserialization is correct.
 * <pre>
 * Java Objects =serialize=> JSON =deserialize=> Target Types instances =serialize=> JSON =deserialize=> Java Objects
 * </pre>
 *
 * Call client language http servers and compare the response with request values.
 * Target language servers simply deserialize the request body and serialize again as the response body.
 * Target servers are implemented in "/client-test" directory.
 * @author Cause Chung
 */
@ExtendWith(ClientServersExtension.class)
final class JsonE2eTest {
    private static final ObjectRemoteClientCaller caller = new ObjectRemoteClientCaller();

    @ParameterizedTest
    @EnumSource(value = TargetCodeType.class, names = "GO")
    void javaClassWithSuperType(TargetCodeType targetCodeType) throws Exception {
        JavaClass obj = new JavaClass();
        obj.setSize(EnumSize.LARGE);
        obj.setString("foo");
        obj.setA(555);
        var res = caller.call(obj, targetCodeType);

        assertThat(res).isEqualTo(obj);
        assertThat(res.getNotIgnoredImplementedMethod()).isEqualTo(1);
    }
}

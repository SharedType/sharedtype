package online.sharedtype.e2e;

import online.sharedtype.it.java8.EnumSize;
import online.sharedtype.it.java8.JavaClass;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test is to check e2e equality to ensure the serialization/deserialization is correct.
 * <pre>
 * Java Objects =serialize=> JSON =deserialize=> Target Types instances =serialize=> JSON =deserialize=> Java Objects
 * </pre>
 *
 * Call client language http servers and compare the response with request values.
 * Client language servers simply deserialize the request body and serialize again as the response body.
 * Client servers are implemented in "/client-test" directory.
 * @author Cause Chung
 */
final class JsonE2eTest {
    private final ObjectRemoteClientCaller goCaller = new ObjectRemoteClientCaller(URI.create("http://localhost:3001/"));

    @Test
    void javaClassWithSuperType() throws Exception {
        JavaClass obj = new JavaClass();
        obj.setSize(EnumSize.LARGE);
        obj.setString("foo");
        obj.setA(555);
        var res = goCaller.call(obj);

        assertThat(res).isEqualTo(obj);
        assertThat(res.getNotIgnoredImplementedMethod()).isEqualTo(1);
    }
}

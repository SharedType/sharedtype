package online.sharedtype.e2e;

import online.sharedtype.it.java8.ArrayClass;
import online.sharedtype.it.java8.CustomList;
import online.sharedtype.it.java8.CustomMap;
import online.sharedtype.it.java8.DependencyClassA;
import online.sharedtype.it.java8.DependencyClassB;
import online.sharedtype.it.java8.DependencyClassC;
import online.sharedtype.it.java8.EnumSize;
import online.sharedtype.it.java8.GenericTypeReifyIssue44;
import online.sharedtype.it.java8.JavaClass;
import online.sharedtype.it.java8.JavaTimeClass;
import online.sharedtype.it.java8.MapClass;
import online.sharedtype.it.java8.OptionalMethod;
import online.sharedtype.processor.domain.TargetCodeType;
import org.assertj.core.api.SoftAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
 * To start the target servers, run "misc/start-client-servers.sh"
 * @author Cause Chung
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JsonE2eTest {
    private final ObjectRemoteClientCaller caller = new ObjectRemoteClientCaller();

    private final Instant NOW = Instant.now();

    @BeforeAll
    void waitForServers() {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).until(() -> caller.isHealthy(TargetCodeType.GO));
    }

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

    @ParameterizedTest
    @EnumSource(value = TargetCodeType.class, names = "GO")
    void javaTimeClass(TargetCodeType targetCodeType) throws Exception {
        var obj = new JavaTimeClass();
        obj.setInstant(NOW);
        obj.setUtilDate(new java.util.Date());
        obj.setSqlDate(new java.sql.Date(NOW.toEpochMilli()));
        obj.setLocalDate(java.time.LocalDate.now());
        obj.setLocalDateTime(java.time.LocalDateTime.now());
        obj.setLocalTime(LocalTime.now());
        obj.setOffsetDateTime(OffsetDateTime.now());
        obj.setOffsetTime(OffsetTime.now());
        obj.setZonedDateTime(ZonedDateTime.now());
        var res = caller.call(obj, targetCodeType);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(res.getInstant()).isEqualTo(obj.getInstant());
            softly.assertThat(res.getUtilDate()).isEqualTo(obj.getUtilDate());
            softly.assertThat(res.getSqlDate()).isEqualTo(obj.getSqlDate());
            softly.assertThat(res.getLocalDate()).isEqualTo(obj.getLocalDate());
            softly.assertThat(res.getLocalDateTime()).isEqualTo(obj.getLocalDateTime());
            softly.assertThat(res.getLocalTime()).isEqualTo(obj.getLocalTime());
            softly.assertThat(res.getOffsetDateTime()).isEqualTo(obj.getOffsetDateTime());
            softly.assertThat(res.getOffsetTime()).isEqualTo(obj.getOffsetTime());
            softly.assertThat(res.getZonedDateTime()).isEqualTo(obj.getZonedDateTime());
        });
    }

    @ParameterizedTest
    @EnumSource(value = TargetCodeType.class, names = "GO")
    void subtypeWithNestedCustomTypeString(TargetCodeType targetCodeType) throws Exception {
        var obj = new GenericTypeReifyIssue44.SubtypeWithNestedCustomTypeString();
        var value = new GenericTypeReifyIssue44.CustomContainer<String>();
        value.setValue("foo");
        obj.setValue(value);
        var res = caller.call(obj, targetCodeType);
        assertThat(res).isEqualTo(obj);
    }

    @ParameterizedTest
    @EnumSource(value = TargetCodeType.class, names = "GO")
    void cyclicDependencyClass(TargetCodeType targetCodeType) throws Exception {
        var objC = new DependencyClassC();
        var objB = new DependencyClassB();
        objB.setC(objC);
        var objA = new DependencyClassA();
        objA.setB(objB);
        objA.setA(100);

        var objA2 = new DependencyClassA();
        objA2.setA(200);
        objC.setA(objA2);

        var res = caller.call(objA, targetCodeType);
        assertThat(res).isEqualTo(objA);
    }

    @ParameterizedTest
    @EnumSource(value = TargetCodeType.class, names = "GO")
    void mapClass(TargetCodeType targetCodeType) throws Exception {
        var obj = new MapClass();
        obj.setMapField(new ConcurrentHashMap<>());
        obj.getMapField().put(5, "bar");
        obj.setEnumKeyMapField(new HashMap<>());
        obj.getEnumKeyMapField().put(EnumSize.LARGE, "bar3");
        obj.setCustomMapField(new CustomMap());
        obj.getCustomMapField().put(33, "bar22");
        obj.setNestedMapField(new HashMap<>());
        obj.getNestedMapField().put("foo", new HashMap<>());
        obj.getNestedMapField().get("foo").put("bar", 5);
        var res = caller.call(obj, targetCodeType);
        assertThat(res).isEqualTo(obj);
    }

    @ParameterizedTest
    @EnumSource(value = TargetCodeType.class, names = "GO")
    void arrayClass(TargetCodeType targetCodeType) throws Exception {
        var obj = new ArrayClass();
        obj.setArr(new CustomList());
        obj.getArr().add("foo");
        var res = caller.call(obj, targetCodeType);
        assertThat(res).isEqualTo(obj);
    }
}

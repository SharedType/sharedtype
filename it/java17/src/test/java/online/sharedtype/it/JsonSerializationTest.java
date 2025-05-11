package online.sharedtype.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import online.sharedtype.it.java17.JavaRecord;
import online.sharedtype.it.java8.Container;
import online.sharedtype.it.java8.DependencyClassA;
import online.sharedtype.it.java8.DependencyClassB;
import online.sharedtype.it.java8.DependencyClassC;
import online.sharedtype.it.java8.EnumGalaxy;
import online.sharedtype.it.java8.EnumSize;
import online.sharedtype.it.java8.OptionalMethod;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JsonSerializationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    {
        objectMapper.registerModule(new Jdk8Module());
    }

    @Test
    void jsonOptionalMethod() throws Exception {
        var obj = new OptionalMethod();
        obj.setValue("value");
        var json = objectMapper.writeValueAsString(obj);
        System.out.println(json);
    }

    @Test
    void test() throws Exception {
        var obj = JavaRecord
            .<String>builder()
            .string("exampleString")
            .primitiveByte((byte) 1)
            .boxedByte((byte) 2)
            .primitiveShort((short) 3)
            .boxedShort((short) 4)
            .primitiveInt(5)
            .boxedInt(6)
            .primitiveLong(7L)
            .boxedLong(8L)
            .primitiveFloat(9.0f)
            .boxedFloat(10.0f)
            .primitiveDouble(11.0)
            .boxedDouble(12.0)
            .primitiveBoolean(true)
            .boxedBoolean(Boolean.TRUE)
            .primitiveChar('a')
            .boxedChar('b')
            .object("object")
            .cyclicDependency(new DependencyClassA())
            .containerStringList(List.of(new Container<>()))
            .containerStringListCollection(List.of(List.of(new Container<>())))
            .genericList(List.of("genericValue"))
            .genericSet(Set.of("genericValue"))
            .genericListSet(List.of(Set.of("genericValue")))
            .intArray(new int[]{1, 2, 3})
            .boxedIntArray(new Integer[]{4, 5, 6})
            .enumGalaxy(EnumGalaxy.MilkyWay)
            .enumSize(EnumSize.LARGE)
            .duplicateAccessor("duplicate")
            .explicitlyIgnored("value should be ignored and empty in response")
            .build();
        obj.cyclicDependency().setA(999);
        obj.containerStringList().get(0).setT("bar");
        obj.containerStringListCollection().get(0).iterator().next().setT("foo");
        var json = objectMapper.writeValueAsString(obj);
        System.out.println(json);
        var deser = objectMapper.readValue(json, JavaRecord.class);
        assertThat(deser).usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("explicitlyIgnored").build())
            .isEqualTo(obj);
    }
}

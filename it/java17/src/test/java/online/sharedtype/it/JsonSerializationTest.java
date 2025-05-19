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
            .build();
        var json = objectMapper.writeValueAsString(obj);
        System.out.println(json);
        var deser = objectMapper.readValue(json, JavaRecord.class);
        assertThat(deser).usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("explicitlyIgnored").build())
            .isEqualTo(obj);
    }
}

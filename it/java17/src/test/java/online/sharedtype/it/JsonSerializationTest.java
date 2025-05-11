package online.sharedtype.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import online.sharedtype.it.java8.OptionalMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

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
}

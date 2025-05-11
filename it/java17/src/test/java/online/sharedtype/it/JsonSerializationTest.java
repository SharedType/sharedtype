package online.sharedtype.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import online.sharedtype.it.java8.DependencyClassA;
import online.sharedtype.it.java8.DependencyClassB;
import online.sharedtype.it.java8.DependencyClassC;
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

    @Test
    void cyclicReference() throws Exception {
        var objC = new DependencyClassC();
        var objB = new DependencyClassB();
        objB.setC(objC);
        var objA = new DependencyClassA();
        objA.setB(objB);
        objA.setA(100);

        var objA2 = new DependencyClassA();
        objA2.setA(200);
        objC.setA(objA2);
        var json = objectMapper.writeValueAsString(objA);
        System.out.println(json);
    }
}

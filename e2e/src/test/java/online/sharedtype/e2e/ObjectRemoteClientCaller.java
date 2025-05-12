package online.sharedtype.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import online.sharedtype.processor.domain.TargetCodeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

final class ObjectRemoteClientCaller {
    private static final Map<TargetCodeType, URI> endpoints = Map.of(
        TargetCodeType.TYPESCRIPT, URI.create("http://localhost:3005/"),
        TargetCodeType.GO, URI.create("http://localhost:3001/"),
        TargetCodeType.RUST, URI.create("http://localhost:3002/")
    );
    private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    {
        objectMapper.registerModules(new JavaTimeModule(), new Jdk8Module());
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigInteger.class, ToStringSerializer.instance);
        module.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        objectMapper.registerModule(module);
    }

    <T> T call(T t, TargetCodeType targetCodeType) throws Exception {
        String simpleName = t.getClass().getSimpleName();
        String json = objectMapper.writeValueAsString(t);

        System.out.printf("Req[%s]: %s%n", t.getClass().getCanonicalName(), json);
        var req = HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(endpoints.get(targetCodeType).resolve(simpleName))
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        var body = response.body();
        System.out.printf("Res[%s]: %s%n", t.getClass().getCanonicalName(), body);
        return (T)objectMapper.readValue(body, t.getClass());
    }

    boolean isHealthy(TargetCodeType targetCodeType) throws Exception {
        var req = HttpRequest.newBuilder()
            .uri(endpoints.get(targetCodeType).resolve("health"))
            .build();
        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        var code = response.statusCode();
        System.out.printf("Health[%s]: %d%n", targetCodeType, code);
        return code >= 200 && code < 400;
    }
}

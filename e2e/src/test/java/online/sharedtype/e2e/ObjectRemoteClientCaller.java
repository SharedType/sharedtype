package online.sharedtype.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import online.sharedtype.SharedType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

final class ObjectRemoteClientCaller {
    private static final Map<SharedType.TargetType, URI> endpoints = Map.of(
        SharedType.TargetType.TYPESCRIPT, URI.create("http://localhost:3005/"),
        SharedType.TargetType.GO, URI.create("http://localhost:3001/"),
        SharedType.TargetType.RUST, URI.create("http://localhost:3002/")
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

    <T> T call(T t, SharedType.TargetType targetCodeType) throws Exception {
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

    boolean isHealthy(SharedType.TargetType targetCodeType) {
        var req = HttpRequest.newBuilder()
            .uri(endpoints.get(targetCodeType).resolve("health"))
            .build();
        HttpResponse<String> response;
        try {
            response = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.printf("Failed health check for %s, request: %s", targetCodeType, req);
            e.printStackTrace();
            return false;
        }
        var code = response.statusCode();
        System.out.printf("Health[%s]: %d%n", targetCodeType, code);
        return code >= 200 && code < 400;
    }
}

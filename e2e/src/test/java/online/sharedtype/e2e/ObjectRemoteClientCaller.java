package online.sharedtype.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.sharedtype.processor.domain.TargetCodeType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

final class ObjectRemoteClientCaller {
    private static final Map<TargetCodeType, URI> endpoints = Map.of(
        TargetCodeType.TYPESCRIPT, URI.create("http://localhost:3000/"),
        TargetCodeType.GO, URI.create("http://localhost:3001/"),
        TargetCodeType.RUST, URI.create("http://localhost:3002/")
    );
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    <T> T call(T t, TargetCodeType targetCodeType) throws Exception {
        String simpleName = t.getClass().getSimpleName();
        String json = objectMapper.writeValueAsString(t);

        var req = HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(endpoints.get(targetCodeType).resolve(simpleName))
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        var body = response.body();
        System.out.printf("Response[%s]: %s%n", t.getClass().getCanonicalName(), body);
        return (T)objectMapper.readValue(body, t.getClass());
    }

    boolean isHealthy(TargetCodeType targetCodeType) throws Exception {
        var req = HttpRequest.newBuilder()
            .uri(endpoints.get(targetCodeType))
            .build();
        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        var code = response.statusCode();
        System.out.printf("Health[%s]: %d%n", targetCodeType, code);
        return code >= 200 && code < 400;
    }
}

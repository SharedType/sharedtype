package online.sharedtype.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
final class ObjectRemoteClientCaller {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final URI hostUri;

    <T> T call(T t) throws Exception {
        String simpleName = t.getClass().getSimpleName();
        String json = objectMapper.writeValueAsString(t);

        var req = HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(hostUri.resolve(simpleName))
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        var body = response.body();
        log(t.getClass().getCanonicalName(), body);
        return (T)objectMapper.readValue(body, t.getClass());
    }

    private static void log(String qualifiedName, String msg) {
        System.out.printf("Response[%s]: %s%n", qualifiedName, msg);
    }
}

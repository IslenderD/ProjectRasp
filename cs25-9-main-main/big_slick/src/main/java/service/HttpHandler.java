package service;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Called when using Http (for sending information to Raspberry).
 */
public class HttpHandler {

    /// Client connection to the Raspberry PI (Can only be one, so static/final). Timeout=How long for considering it
    /// not reachable. Version = Type of connection, may change later.
    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).
            version(HttpClient.Version.HTTP_1_1).build();

    /// URL of the Raspberry Pi server, will change on future.
    private static final String RASPBERRY_URL = "http://localhost:8080";

    /**
     * Returns HttpClient related to the Raspberry Pi.
     * @return client
     */
    public static HttpClient getClient() {
        return CLIENT;
    }

    /**
     * Returns url of the Raspberry Pi server.
     * @return url
     */
    public static String getRaspberryUrl() {
        return RASPBERRY_URL;
    }

    /**
     * POST request to the Raspberry Pi.
     * @param endpoint address of the endpoint on the Raspberry Pi (i.e /move)
     * @param bean Bean that it's going to be transformed into a json
     * @return request to be later sent
     */
    public static HttpRequest sendJson(String endpoint, Object bean) {
        String finalurl = RASPBERRY_URL + endpoint;
        String json = new Gson().toJson(bean);

        // Creates a request, makes the url understandable for Java (URI), tells it's type its json, transforms json to String with the post (thanks to the previous it will know its json).
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(finalurl)).
                header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

        return request; //When used, do httpHandler.getClient().send(sendJson(endpoint,bean), HttpResponse.BodyHandlers.ofString());
    }
}

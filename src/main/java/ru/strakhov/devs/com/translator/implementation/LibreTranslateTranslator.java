package ru.strakhov.devs.com.translator.implementation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.strakhov.devs.com.translator.Translator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LibreTranslateTranslator extends Translator {
    private static final Logger log = LoggerFactory.getLogger(LibreTranslateTranslator.class.getName());
    private final String endpoint = "http://localhost:5000/translate";

    @Override
    public String translate(String text, String sourceLang, String targetLang) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("q", text);
        jsonBody.addProperty("source", sourceLang);
        jsonBody.addProperty("target", targetLang);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .uri(URI.create(this.endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            log.info("{} response", response.body());

            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            return jsonResponse.get("translatedText").getAsString();
        } catch (IOException | InterruptedException e) {
            log.warn("Translation error, step to initial version", e);
            return text;
        }
    }
}

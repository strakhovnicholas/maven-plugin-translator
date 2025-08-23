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

public class DeepseekTranslator extends Translator {
    private static final Logger log = LoggerFactory.getLogger(DeepseekTranslator.class.getName());

    private final String apiKey;
    private final String endpoint;

    public DeepseekTranslator(String apiKey, String endpoint) {
        this.apiKey = apiKey;
        this.endpoint = endpoint != null ? endpoint : "https://api.deepseek.com/v1/translate";
    }

    @Override
    public String translate(String text, String sourceLang, String targetLang) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("text", text);
        jsonBody.addProperty("source_lang", sourceLang);
        jsonBody.addProperty("target_lang", targetLang);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));

        if (apiKey != null && !apiKey.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + apiKey);
        }

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        try {
            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            log.info("Deepseek response: {}", response.body());

            if (response.statusCode() != 200) {
                log.warn("Deepseek API returned non-200 status: {}", response.statusCode());
                return text;
            }

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            return jsonResponse.has("translated_text")
                    ? jsonResponse.get("translated_text").getAsString()
                    : text;

        } catch (IOException | InterruptedException e) {
            log.warn("Translation error, returning original text", e);
            return text;
        }
    }
}

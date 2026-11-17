package ru.study.weather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Выполняет HTTP-запросы к API Яндекс Погоды. */
public final class YandexWeatherClient {
    private static final String FORECAST_URL = "https://api.weather.yandex.ru/v2/forecast";
    private final HttpClient httpClient;

    public YandexWeatherClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public HttpResponse<String> fetchForecast(WeatherConfig config)
            throws IOException, InterruptedException {
        return httpClient.send(createRequest(config),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    HttpRequest createRequest(WeatherConfig config) {
        URI uri = URI.create(FORECAST_URL + "?lat=" + config.getLatitude()
                + "&lon=" + config.getLongitude() + "&limit=" + config.getLimit());
        return HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("X-Yandex-Weather-Key", config.getApiKey())
                .GET()
                .build();
    }
}

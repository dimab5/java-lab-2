package ru.study.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

/** Автономная проверка без ключа и запросов к платному API. */
public final class WeatherChecks {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        WeatherParser parser = new WeatherParser();
        JsonNode sample = mapper.readTree("""
                {
                  "fact": {"temp": -5, "feels_like": -12},
                  "forecasts": [
                    {"parts": {"night": {"temp_avg": -20}, "day": {"temp_avg": -3}}},
                    {"parts": {"day": {"temp_avg": 0}}},
                    {"parts": {"day": {"temp_avg": 5}}},
                    {"parts": {"day": {"temp_avg": 100}}}
                  ]
                }
                """);
        check(parser.parse(sample.toString(), 3).getCurrentTemperature() == -5, "fact.temp");
        check(Math.abs(parser.parse(sample.toString(), 3).calculateAverageTemperature() - 2.0 / 3) < 1e-10,
                "Среднее только по первым limit дням, с дробной частью");
        check(parser.parse(sample.toString(), 1).calculateAverageTemperature() == -3, "Прогноз за один день");
        HttpRequest request = new YandexWeatherClient().createRequest(
                new WeatherConfig("test-key", 55.75, 37.62, 3));
        check(request.uri().toString().equals(
                "https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=3"), "URL");
        check(request.method().equals("GET"), "Метод GET");
        check(request.headers().firstValue("X-Yandex-Weather-Key").orElseThrow().equals("test-key"),
                "Заголовок с ключом");
        List<Double> temperatures = new ArrayList<>(List.of(1.0, 3.0));
        WeatherReport report = new WeatherReport(5, temperatures);
        temperatures.clear();
        check(report.getForecastDays() == 2 && report.calculateAverageTemperature() == 2,
                "Защитная копия температур");
        expectIOException(() -> parser.parse("invalid-json", 1));
        expectIOException(() -> parser.parse("", 1));
        expectIOException(() -> parser.parse(sample.toString(), 5));
        expectIOException(() -> parser.parse("{\"fact\":{}}", 1));
        expectIOException(() -> parser.parse(
                "{\"fact\":{\"temp\":1},\"forecasts\":[{\"parts\":{\"day\":{\"temp_avg\":null}}}]}", 1));
        expectIOException(() -> parser.parse(
                "{\"fact\":{\"temp\":1},\"forecasts\":[{\"parts\":{\"day\":{\"temp_avg\":\"5\"}}}]}", 1));
        try {
            new WeatherConfig("test-key", Double.NaN, 37.62, 3);
            throw new AssertionError("Некорректная широта принята");
        } catch (IllegalArgumentException expected) {
            // Ожидаемая проверка входных данных.
        }
        System.out.println("Все проверки пройдены.");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    private static void expectIOException(CheckedAction action) throws Exception {
        try {
            action.run();
            throw new AssertionError("Ожидалась ошибка неполного ответа");
        } catch (IOException expected) {
            // Некорректные данные не должны превращаться в нулевую температуру.
        }
    }

    private interface CheckedAction {
        void run() throws Exception;
    }
}

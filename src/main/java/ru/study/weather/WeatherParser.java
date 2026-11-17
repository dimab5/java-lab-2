package ru.study.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Преобразует JSON API в объект погодных данных. */
public final class WeatherParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherReport parse(String json, int limit) throws IOException {
        if (limit < 1 || limit > 7) {
            throw new IllegalArgumentException("Количество суток должно быть от 1 до 7.");
        }
        JsonNode root = objectMapper.readTree(json);
        if (root == null) {
            throw new IOException("API вернул пустой JSON.");
        }
        double currentTemperature = number(root.at("/fact/temp"), "fact.temp");
        JsonNode forecasts = root.get("forecasts");
        if (forecasts == null || !forecasts.isArray() || forecasts.size() < limit) {
            throw new IOException("В ответе недостаточно прогнозов для " + limit + " суток.");
        }
        List<Double> dayTemperatures = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            dayTemperatures.add(number(forecasts.get(i).at("/parts/day/temp_avg"),
                    "forecasts[" + i + "].parts.day.temp_avg"));
        }
        return new WeatherReport(currentTemperature, dayTemperatures);
    }

    private double number(JsonNode node, String field) throws IOException {
        if (!node.isNumber() || !Double.isFinite(node.doubleValue())) {
            throw new IOException("Поле " + field + " отсутствует или не содержит число.");
        }
        return node.doubleValue();
    }
}

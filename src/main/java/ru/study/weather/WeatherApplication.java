package ru.study.weather;

import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpResponse;
import java.util.Locale;

/** Управляет получением данных и выводом результатов в консоль. */
public final class WeatherApplication {
    private final YandexWeatherClient client;
    private final WeatherParser parser;
    private final PrintStream output;

    public WeatherApplication(YandexWeatherClient client, WeatherParser parser, PrintStream output) {
        this.client = client;
        this.parser = parser;
        this.output = output;
    }

    public void run(WeatherConfig config) throws IOException, InterruptedException {
        HttpResponse<String> response = client.fetchForecast(config);

        output.print(response.body());
        output.println();
        if (response.statusCode() != 200) {
            throw new IOException("API вернул HTTP " + response.statusCode()
                    + ". Проверьте ключ, доступ к REST v2 и ограничения тарифа.");
        }

        WeatherReport report = parser.parse(response.body(), config.getLimit());
        output.printf(Locale.ROOT, "Текущая температура (fact.temp): %s °C%n",
                report.getCurrentTemperature());
        output.printf(Locale.ROOT, "Средняя дневная температура за %d суток: %.2f °C%n",
                report.getForecastDays(), report.calculateAverageTemperature());
    }
}

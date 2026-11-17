package ru.study.weather;

import java.io.IOException;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        try {
            WeatherConfig config = WeatherConfig.fromArgs(args);
            WeatherApplication application = new WeatherApplication(
                    new YandexWeatherClient(), new WeatherParser(), System.out);
            application.run(config);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Запрос прерван.");
            System.exit(1);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(1);
        }
    }
}

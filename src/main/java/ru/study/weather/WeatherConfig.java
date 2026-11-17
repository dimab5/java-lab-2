package ru.study.weather;

/** Настройки запроса. После создания объект нельзя изменить. */
public final class WeatherConfig {
    // апи ключ задается здесь или через перменные окружения
    private static final String API_KEY = "";

    private final String apiKey;
    private final double latitude;
    private final double longitude;
    private final int limit;

    public WeatherConfig(String apiKey, double latitude, double longitude, int limit) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Широта должна быть от -90 до 90.");
        }
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Долгота должна быть от -180 до 180.");
        }
        if (limit < 1 || limit > 7) {
            throw new IllegalArgumentException("Количество суток должно быть от 1 до 7.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Укажите ключ в API_KEY в WeatherConfig.java или в переменной окружения YANDEX_WEATHER_KEY.");
        }
        this.apiKey = apiKey.trim();
        this.latitude = latitude;
        this.longitude = longitude;
        this.limit = limit;
    }

    public static WeatherConfig fromArgs(String[] args) {
        if (args.length != 0 && args.length != 3) {
            throw new IllegalArgumentException("Использование: Main [широта долгота количество_суток]");
        }
        double latitude = args.length == 0 ? 55.75 : Double.parseDouble(args[0]);
        double longitude = args.length == 0 ? 37.62 : Double.parseDouble(args[1]);
        int limit = args.length == 0 ? 3 : Integer.parseInt(args[2]);
        String key = System.getenv("YANDEX_WEATHER_KEY");
        if (key == null || key.isBlank()) {
            key = API_KEY;
        }
        return new WeatherConfig(key, latitude, longitude, limit);
    }

    public String getApiKey() {
        return apiKey;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getLimit() {
        return limit;
    }
}

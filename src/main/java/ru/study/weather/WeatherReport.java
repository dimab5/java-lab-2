package ru.study.weather;

import java.util.List;

/** Погодные данные и вычисления над ними, независимо от формата JSON и HTTP. */
public final class WeatherReport {
    private final double currentTemperature;
    private final List<Double> dayTemperatures;

    public WeatherReport(double currentTemperature, List<Double> dayTemperatures) {
        if (!Double.isFinite(currentTemperature)) {
            throw new IllegalArgumentException("Текущая температура должна быть конечным числом.");
        }
        if (dayTemperatures == null || dayTemperatures.isEmpty()) {
            throw new IllegalArgumentException("Нужна хотя бы одна дневная температура.");
        }
        for (Double temperature : dayTemperatures) {
            if (temperature == null || !Double.isFinite(temperature)) {
                throw new IllegalArgumentException("Дневная температура должна быть конечным числом.");
            }
        }
        this.currentTemperature = currentTemperature;
        this.dayTemperatures = List.copyOf(dayTemperatures);
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public int getForecastDays() {
        return dayTemperatures.size();
    }

    public double calculateAverageTemperature() {
        double sum = 0;
        for (double temperature : dayTemperatures) {
            sum += temperature;
        }
        return sum / dayTemperatures.size();
    }
}

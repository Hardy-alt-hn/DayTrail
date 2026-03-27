package com.example.daytrail.data;

public enum Weather {
    SUNNY("sunny"),
    CLOUDY("cloudy"),
    RAINY("rainy"),
    SNOWY("snowy");

    private final String value;

    Weather(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Weather fromValue(String value) {
        for (Weather weather : Weather.values()) {
            if (weather.value.equals(value)) {
                return weather;
            }
        }
        return SUNNY;
    }
}

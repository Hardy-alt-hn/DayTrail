package com.example.daytrail.data;

public enum Weather {
    SUNNY("晴"),
    CLOUDY("多云"),
    RAINY("雨"),
    SNOWY("雪");

    private final String displayName;

    Weather(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
    
    public String getValue() {
        return displayName;
    }
    
    public static Weather fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return SUNNY;
        }
        
        for (Weather weather : Weather.values()) {
            if (weather.displayName.equals(value)) {
                return weather;
            }
        }
        
        // 默认返回 SUNNY
        return SUNNY;
    }
}

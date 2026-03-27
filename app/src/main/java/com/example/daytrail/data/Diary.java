
package com.example.daytrail.data;

public class Diary {
    private long id;
    private long userId;
    private String title;
    private String content;
    private long date;
    private Weather weather;

    public Diary(long userId, String title, String content, Weather weather) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.date = System.currentTimeMillis();
        this.weather = weather;
    }

    public Diary(long id, long userId, String title, String content, long date, Weather weather) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.weather = weather;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
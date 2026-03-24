
package com.example.daytrail.data;

public class Diary {
    private long id;
    private String title;
    private String content;
    private long date;
    private String weather;

    public Diary(String title, String content, String weather) {
        this.title = title;
        this.content = content;
        this.date = System.currentTimeMillis();
        this.weather = weather;
    }

    public Diary(long id, String title, String content, long date, String weather) {
        this.id = id;
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

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
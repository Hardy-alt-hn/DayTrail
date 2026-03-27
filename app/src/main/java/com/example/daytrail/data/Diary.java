
package com.example.daytrail.data;

public class Diary {
    private long id;
    private String title;
    private String content;
    private long date;
    private String weather;
    private long categoryId;
    private String categoryName;

    public Diary(String title, String content, String weather) {
        this.title = title;
        this.content = content;
        this.date = System.currentTimeMillis();
        this.weather = weather;
        this.categoryId = 1; // 默认为"未分类"
    }

    public Diary(long id, String title, String content, long date, String weather) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.weather = weather;
        this.categoryId = 1;
    }

    public Diary(long id, String title, String content, long date, String weather, long categoryId, String categoryName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.weather = weather;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
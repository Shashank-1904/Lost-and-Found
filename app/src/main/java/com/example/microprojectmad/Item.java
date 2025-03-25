package com.example.microprojectmad;

public class Item {
    private String title;
    private String date;

    public Item(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}

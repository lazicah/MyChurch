package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 4/14/18.
 */

public class Sermon {


    int id;
    private String title;
    private String name;
    private String email;
    private String text;
    private int views;
    private String date;
    public Sermon(int id, String title, String name, String email, String text, String date) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.email = email;
        this.text = text;

        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

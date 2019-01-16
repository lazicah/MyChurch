package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 5/14/18.
 */

public class Announcements {

    public Announcements() {
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }



    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Announcements(String text, int id) {
        this.text = text;
        this.id = id;
    }

    private int id;
}

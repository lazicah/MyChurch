package com.disciplesbay.latterhousehq.mychurch.data;

import com.disciplesbay.latterhousehq.mychurch.R;

/**
 * Created by root on 5/8/18.
 */

public class SermonCategory {

    public SermonCategory(String category, String color, int img, int id) {
        this.category = category;
        this.color = color;
        this.img = img;
        this.id = id;
    }

    public SermonCategory(String category, long id) {
        this.category = category;
        this.id = id;
    }

    public SermonCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public SermonCategory() {
    }

    public void setId(long id) {
        this.id = id;
    }

    private String category;
    private String color;
    private int img;
    private long id;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getCategory() {
        return category;

    }

    public String getColor() {
        return color;
    }

    public int getImg() {
        return img;
    }
}

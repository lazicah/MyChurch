package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 6/19/18.
 */

public class Books {
    private String filename;
    private String filepath;
    private String url;
    private String timestamp;
    private String description;

    public Books(String filename, String url, String timestamp, String description, double price, Boolean isPaid, int id) {
        this.filename = filename;
        this.url = url;
        this.timestamp = timestamp;
        this.description = description;
        this.price = price;
        this.isPaid = isPaid;
        this.id = id;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    private double price;
    private Boolean isPaid;
    private int id;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Books(String filename, String filepath, int id) {
        this.filename = filename;
        this.filepath = filepath;
        this.id = id;
    }
}

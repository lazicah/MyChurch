package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 5/15/18.
 */

public class AudioVideo {

    public static final int VIDEO=0;

    private int id;
    private String url;
    private String name, timestamp;
    private String description;
    private double price;
    private Boolean isPaid;
    private String filepath;
    private String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getFilepath() {
        return filepath;
    }

    public AudioVideo(String name, String filepath) {
        this.name = name;
        this.filepath = filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;

    }

    private String type;

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public AudioVideo(int id, String url, String name, String timestamp, String description,String thumb, double price, boolean isPaid, String type) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.timestamp = timestamp;
        this.description = description;
        this.price = price;
        this.isPaid = isPaid;
        this.type = type;
        this.thumb = thumb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AudioVideo() {
    }

    public AudioVideo(int id, String url, String name, String description, String timestamp, double price) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.timestamp = timestamp;
        this.price = price;
        this.description = description;


    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



}

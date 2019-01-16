package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 7/14/18.
 */

public class Comments {

    private int id;
    private String Username;
    private String Timestamp;
    private String Comment;
    private int color = -1;

    public Comments(int id, String username, String timestamp, String comment, int color) {
        this.id = id;
        Username = username;
        Timestamp = timestamp;
        Comment = comment;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

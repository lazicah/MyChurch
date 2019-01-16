package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 4/10/18.
 */

public class Testimony {


    String likes;
    String reactionId;
    private String testId;
    private String deleteId;
    private String username;
    private String id;
    private String image;
    private Boolean isLiked;
    private String description;
    private String date;
    private int color = -1;
    private String title;

    public Testimony(String image, String testId,String id,String username,   String description, String date,int color,String likes,Boolean isLiked, String deleteId) {
        this.likes = likes;
        this.username = username;
        this.id = id;
        this.isLiked = isLiked;
        this.description = description;
        this.date = date;
        this.color = color;
        this.testId = testId;
        this.image = image;
        this.deleteId = deleteId;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(String deleteId) {
        this.deleteId = deleteId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public String getReactionId() {
        return reactionId;
    }

    public void setReactionId(String reactionId) {
        this.reactionId = reactionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 4/29/18.
 */

public class GivingData {
    public String getGivingName() {
        return givingName;
    }

    public void setGivingName(String givingName) {
        this.givingName = givingName;
    }


    public GivingData(String givingName, int imageResource, int color) {
        this.givingName = givingName;
        this.imageResource = imageResource;
        this.color = color;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private String givingName;
    private int imageResource;
    private int color;

    public int getColor() {
        return color;
    }
}

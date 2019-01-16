package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 5/5/18.
 */

public class EventsData {



    public static final int EVENT=1;
    public static final int ANNOUNCE=0;

    private String eventName;
    private String eventDesrciption;
    private int id;
    private String image;
    private String url;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String expires;




    public int type;

    public EventsData(String image,String eventName, String eventDesrciption, int id, String expires, int type) {
        this.eventName = eventName;
        this.eventDesrciption = eventDesrciption;
        this.id = id;
        this.expires = expires;
        this.type = type;
        this.image = image;
    }
    public EventsData(String image,String loc,String eventName, String eventDesrciption, int id, String expires, int type) {
        this.eventName = eventName;
        this.eventDesrciption = eventDesrciption;
        this.id = id;
        this.expires = expires;
        this.type = type;
        this.image = image;
        this.location=loc;
    }




    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public EventsData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesrciption() {
        return eventDesrciption;
    }

    public void setEventDesrciption(String eventDesrciption) {
        this.eventDesrciption = eventDesrciption;
    }
}

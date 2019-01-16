package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by root on 5/19/18.
 */

public class LocationData {
    public LocationData(String location, String gmmIntentUri) {
        this.location = location;
        this.gmmIntentUri = gmmIntentUri;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setGmmIntentUri(String gmmIntentUri) {
        this.gmmIntentUri = gmmIntentUri;
    }

    private String location;

    public String getGmmIntentUri() {
        return gmmIntentUri;
    }

    private String gmmIntentUri;
}

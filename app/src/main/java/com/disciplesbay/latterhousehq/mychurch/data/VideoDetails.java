package com.disciplesbay.latterhousehq.mychurch.data;

/**
 * Created by Ansh on 29/10/2017 12:26 AM.
 */

public class VideoDetails {

    public static final int VIDEO=1;
    public static final int DEFAULT=0;

    private String VideoName;
    private String VideoDesc;
    private String URL;
    private String VideoId;
    public int type;

    public VideoDetails(String videoName, String videoDesc, String URL, String videoId, int type) {
        VideoName = videoName;
        VideoDesc = videoDesc;
        this.URL = URL;
        VideoId = videoId;
        this.type = type;
    }

    public VideoDetails() {
    }

    public void setVideoName(String VideoName){
    this.VideoName=VideoName;
}

public String getVideoName(){
    return VideoName;
}

public void setVideoDesc(String VideoDesc){
    this.VideoDesc=VideoDesc;
}

public String getVideoDesc(){
    return VideoDesc;
}

public void setURL(String URL){
    this.URL=URL;
}

public String getURL(){
    return URL;
}

public void setVideoId(String VideoId){
    this.VideoId=VideoId;
}
public String getVideoId(){
    return VideoId;
}

}

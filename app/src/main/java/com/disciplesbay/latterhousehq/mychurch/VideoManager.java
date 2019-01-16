package com.disciplesbay.latterhousehq.mychurch;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 6/27/18.
 */

public class VideoManager {
    // SDCard Path
    final String MEDIA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    private ArrayList<AudioVideo> songsList = new ArrayList<>();

    // Constructor
    public VideoManager() {


    }

    /**
     * Function to read all audio and video files from sdcard
     * and store the details in ArrayList
     */
    public ArrayList<AudioVideo> getPlayList(Activity activity) {
        Context context = activity.getApplicationContext();
        String sdCard = "";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdCard = context.getFilesDir().toString();
        }else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){
            sdCard = Environment.getExternalStorageDirectory().toString();
        }


        File home = new File(MEDIA_PATH);



            /* if specified not exist create new */
        if (!home.exists()) {
            home.mkdir();
            Log.v("", "inside mkdir");
        }

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                int index = file.getName().lastIndexOf(".");
                String checkType = file.getName().substring(index + 1);
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
                if (checkType.equalsIgnoreCase("chu")){
                    song.put("songType", "audio");
                }else if (checkType.equalsIgnoreCase("mp4")){
                    song.put("songType","video");
                }


                // Adding each song to SongList
                songsList.add(new AudioVideo(file.getName().substring(0, (file.getName().length() - 4)),file.getPath()));

            }
        }


        // return songs list array
        return songsList;
    }

    /**
     * Class to filter files which are having .mp3 extension
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".chv") || name.endsWith(".mp4"));
        }
    }
}

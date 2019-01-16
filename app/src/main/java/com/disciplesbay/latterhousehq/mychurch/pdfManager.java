package com.disciplesbay.latterhousehq.mychurch;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.disciplesbay.latterhousehq.mychurch.data.Books;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 6/19/18.
 */

public class pdfManager {
    // SDCard Path
    final String MEDIA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();


    private ArrayList<Books> songsList = new ArrayList<>();

    // Constructor
    public pdfManager() {


    }

    /**
     * Function to read all audio and video files from sdcard
     * and store the details in ArrayList
     */
    public ArrayList<Books> getPlayList(Activity activity) {
        Context context = activity.getApplicationContext();
        String sdCard = "";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdCard = context.getFilesDir().toString();
        }else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){
            sdCard = Environment.getExternalStorageDirectory().toString();
        }


        File home = new File(sdCard, ".LatterHouse");



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
                }else if (checkType.equalsIgnoreCase("chv")){
                    song.put("songType","video");
                }


                // Adding each song to SongList
                songsList.add(new Books(file.getName().substring(0, (file.getName().length() - 4)),file.getPath(),1));
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
            return (name.endsWith(".pdf") || name.endsWith(".pdf"));
        }
    }
}

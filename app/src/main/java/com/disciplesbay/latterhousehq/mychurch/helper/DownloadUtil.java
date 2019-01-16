package com.disciplesbay.latterhousehq.mychurch.helper;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.disciplesbay.latterhousehq.mychurch.DownloadActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by root on 7/15/18.
 */

public class DownloadUtil {

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    int id = 1;
    String[] urlsToDownload = new String[1];
    int counter = 0;
    ArrayList<AsyncTask<String, String, Void>> arr;
    int totalSize = 1;
    private DownloadUtil.NotificationReceiver nReceiver;
    private TextView name, size;


    private Activity activity;

    public DownloadUtil(Activity activity, String[] urlsToDownload) {
        this.activity = activity;
        this.urlsToDownload = urlsToDownload;
    }

    private void downloadImagesToSdCard(String downloadUrl, String imageName) {
        FileOutputStream fos;
        InputStream inputStream = null;

        try {
            URL url = new URL(downloadUrl);
            /* making a directory in sdcard */
            String subFolder = activity.getPackageName();
            String sdCard = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                sdCard = activity.getApplicationContext().getFilesDir().toString();
            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                sdCard = Environment.getExternalStorageDirectory().toString();
            }


            File myDir = new File(sdCard, ".LatterHouse");

            /* if specified not exist create new */
            if (!myDir.exists()) {
                myDir.mkdir();
                Log.v("", "inside mkdir");
            }

            /* checks the file and if it already exist delete */
            String fname = imageName;
            File file = new File(myDir, fname);
            Log.d("file===========path", "" + file);
            if (file.exists())
                file.delete();


            /* Open a connection */
            URLConnection ucon = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            inputStream = httpConn.getInputStream();

            /*
             * if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
             * inputStream = httpConn.getInputStream(); }
             */

            fos = new FileOutputStream(file);
            int totalSize = httpConn.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i("Progress:", "downloadedSize:" + downloadedSize +
                "totalSize:" + totalSize);
            }
            inputStream.close();
            fos.close();
            Log.d("test", "Image Saved in sdcard..");
        } catch (IOException io) {
            inputStream = null;
            fos = null;
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    public String changeExtension(String path, String extensionAudio, String extensionVideo, String extensionPdf) {
        String filename = path;
        String extension = "";

        int fileTypr = path.lastIndexOf(".");
        String checkExt = path.substring(fileTypr + 1);
        if (checkExt.equalsIgnoreCase("mp3")) {

            if (filename.contains(".")) {
                filename = filename.substring(0, filename.lastIndexOf('.'));
                extension = extensionAudio;

            }


        } else if (checkExt.equalsIgnoreCase("mp4")) {
            if (filename.contains(".")) {
                filename = filename.substring(0, filename.lastIndexOf('.'));
                extension = extensionVideo;
            }

        }else if (checkExt.equalsIgnoreCase("pdf")) {
            if (filename.contains(".")) {
                filename = filename.substring(0, filename.lastIndexOf('.'));
                extension = extensionPdf;
            }

        }

        filename += "." + extension;

        return filename;
    }

    private void killTasks() {
        if (null != arr & arr.size() > 0) {
            for (AsyncTask<String, String, Void> a : arr) {
                Log.i("NotificationReceiver", "Killing download thread");
                a.cancel(true);
            }
        }
        mNotifyManager.cancelAll();
    }

    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getExtras().getString(NLService.NOT_EVENT_KEY);
            Log.i("NotificationReceiver", "NotificationReceiver onRecieve : " + event);
            if (event.trim().contentEquals(NLService.NOT_REMOVED)) {
                killTasks();
            }
        }
    }

    private class ImageDownloader extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... param) {

            int lastIndex = param[0].lastIndexOf("/");
            String downloadFileName = param[0].substring(lastIndex + 1);

            String newName = changeExtension(downloadFileName, "chu", "chv","pdf");
            downloadImagesToSdCard(param[0],  newName);
            return null;
        }

        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("Async-Example", "onPostExecute Called");

            float len = urlsToDownload.length;
            // When the loop is finished, updates the notification
            if (counter >= len - 1) {
                mBuilder.setContentTitle("Done.");
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            } else {
                int per = (int) (((counter + 1) / len) * 100f);
                Log.i("Counter", "Counter : " + counter + ", per : " + per);
                mBuilder.setContentText("Downloaded (" + per + "/100");
                mBuilder.setProgress(100, per, false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(id, mBuilder.build());
            }
            counter++;
            Toast.makeText(activity.getApplicationContext(), "Download Completed, Check \"My Downloads\"", Toast.LENGTH_LONG).show();



        }

    }
}

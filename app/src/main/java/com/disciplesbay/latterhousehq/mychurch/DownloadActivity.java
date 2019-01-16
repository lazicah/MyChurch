package com.disciplesbay.latterhousehq.mychurch;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.disciplesbay.latterhousehq.mychurch.helper.NLService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    EditText url;
    Button button;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    int id = 1;
    String[] urlsToDownload = new String[1];
    int counter = 0;
    ArrayList<AsyncTask<String, String, Void>> arr;
    int totalSize = 1;
    private NotificationReceiver nReceiver;
    private TextView name, size;

    public static long getFileSize(String fileUrl) throws IOException {

        URL oracle = new URL(fileUrl);

        HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();

        long fileSize = 0;
        try {
            // retrieve file size from Content-Length header field
            fileSize = Long.parseLong(yc.getHeaderField("Content-Length"));
        } catch (NumberFormatException nfe) {
        }

        return fileSize;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        url = (EditText) findViewById(R.id.download_manager_url_editor);
        button = (Button) findViewById(R.id.download_manager_start_button);
        name = (TextView) findViewById(R.id.file_name);
        size = (TextView) findViewById(R.id.file_size);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Donwloading......").setContentText("Download in progress").setSmallIcon(android.R.mipmap.sym_def_app_icon);
        //Start a lengthy operation in a background thread
        mBuilder.setProgress(0, 0, true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);

        arr = new ArrayList<AsyncTask<String, String, Void>>();
        ImageDownloader imageDownloader = new ImageDownloader();
        arr.add(imageDownloader);

        Intent i = getIntent();
        url.setText(i.getExtras().get("URL").toString());
        name.setText(i.getExtras().get("file_name").toString());




        size.setText("Size: " + String.valueOf(totalSize) + "MB");
        size.setVisibility(View.INVISIBLE);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlsToDownload[0] = url.getText().toString();
                arr = new ArrayList<AsyncTask<String, String, Void>>();
                int incr;
                for (incr = 0; incr < urlsToDownload.length; incr++) {
                    ImageDownloader imageDownloader = new ImageDownloader();
                    imageDownloader.execute(urlsToDownload[incr]);
                    arr.add(imageDownloader);
                }
                mNotifyManager.notify(id, mBuilder.build());
                Toast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_LONG).show();

                button.setEnabled(false);

            }
        });


        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();

        // check to see if the enabledNotificationListeners String contains our
        // package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app
            // the Notification access permission
            // Check if notification is enabled for this application
            Log.i("ACC", "Don't Have Notification access");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {
            Log.i("ACC", "Have Notification access");
        }

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NLService.NOT_TAG);
        registerReceiver(nReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killTasks();
        unregisterReceiver(nReceiver);
    }

    private void downloadImagesToSdCard(String downloadUrl, String imageName) {
        FileOutputStream fos;
        InputStream inputStream = null;

        try {
            URL url = new URL(downloadUrl);
            /* making a directory in sdcard */
            String subFolder = getPackageName();
            String sdCard = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                sdCard = getApplicationContext().getFilesDir().toString();
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
            // int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                // downloadedSize += bufferLength;
                // Log.i("Progress:", "downloadedSize:" + downloadedSize +
                // "totalSize:" + totalSize);
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
            Toast.makeText(getApplicationContext(), "Download Completed, Check \"My Downloads\"", Toast.LENGTH_LONG).show();

            finish();

        }

    }
}

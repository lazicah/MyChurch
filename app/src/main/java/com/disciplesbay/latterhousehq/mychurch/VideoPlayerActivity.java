package com.disciplesbay.latterhousehq.mychurch;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.disciplesbay.latterhousehq.mychurch.app.Utilities;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayerActivity extends AppCompatActivity {


    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private VideoManager songManager;
    private Utilities utils;
    private ArrayList<AudioVideo> songsList = new ArrayList<AudioVideo>();
    private int currentSongIndex = 0;
    private TextView textView;
    String path, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoView = (VideoView) findViewById(R.id.videoView);
        textView = (TextView) findViewById(R.id.songTitle);










        // Mediaplayer

        songManager = new VideoManager();
        utils = new Utilities();

        // Getting all songs list





        Intent intent = getIntent();
        path = intent.getExtras().getString("songIndex");
        name = intent.getExtras().getString("songName");
        textView.setText(name);





        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(VideoPlayerActivity.this);

            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(videoView);
            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }

        try {
            // ID of video file.
            int id = this.getRawResIdByName("myvideo");
            videoView.setVideoURI(Uri.parse(path));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        // When the video file ready for playback.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {

                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }

                // When video Screen change size.
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        // Re-Set the videoView that acts as the anchor for the MediaController
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });




    }

    // Find ID corresponding to the name of the resource (in the directory raw).
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
        Log.i("AndroidVideoView", "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }


    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
        videoView.pause();
    }


    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        videoView.seekTo(position);
        videoView.start();
    }


}

package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.AndroidBuildingMusicPlayerActivity;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.VideoPlayerActivity;
import com.disciplesbay.latterhousehq.mychurch.Utils.VideoThumbLoader;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 7/1/18.
 */

public class DownloadedMediaAdapter extends RecyclerView.Adapter<DownloadedMediaAdapter.MyViewHolder> {
    ArrayList<AudioVideo> singletons;
    Activity activity;
    int total_types;
    private LayoutInflater inflater;


    public DownloadedMediaAdapter(Activity activity, ArrayList<AudioVideo> singletons) {
        this.singletons = singletons;
        this.activity = activity;
        total_types = singletons.size();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.big_video, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        AudioVideo singleton = (AudioVideo) this.singletons.get(position);

        File file = new File(singleton.getFilepath());
        int index = file.getName().lastIndexOf(".");
        String checkType = file.getName().substring(index + 1);

        MyViewHolder holder1 = ((MyViewHolder) holder);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(singleton.getFilepath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        holder1.title.setText(singleton.getName());

        holder1.image.setTag(singleton.getFilepath());

        if (checkType.equalsIgnoreCase("mp4")){
            thumbLoader.showThumbByAsynctack(singleton.getFilepath(),holder1.image);
            long millis = getDuration(file);

            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            holder1.dur.setText(hms);

        }else {
            holder1.image.setImageResource(R.drawable.bird);
                 long millis = getDuration(file);


            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            holder1.dur.setText(hms);
        }




    }


    @Override
    public int getItemCount() {
        return singletons.size();
    }

    private long getDuration(File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(activity, Uri.fromFile(videoFile));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);

        retriever.release();
        return timeInMillisec;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, dur, pubDate, url;
        ImageView image;


        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.name_of);
            image = (ImageView) itemView.findViewById(R.id.image);
            dur = (TextView) itemView.findViewById(R.id.duration);


            ((CardView) itemView.findViewById(R.id.card_view1)).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    Log.d("pathh", singletons.get(getAdapterPosition()).getFilepath());

                    int postion = getAdapterPosition();
                    String filename = singletons.get(getAdapterPosition()).getName();

                    String path = singletons.get(getAdapterPosition()).getFilepath();
                    int fileTypr = path.lastIndexOf(".");
                    String checkExt = path.substring(fileTypr + 1);

                    if (checkExt.equalsIgnoreCase("mp3")) {

                        // Starting new intent
                        Intent in = new Intent(activity, AndroidBuildingMusicPlayerActivity.class);
                        in.putExtra("songIndex", path);
                        in.putExtra("songName", filename);
                        // Closing PlayListView
                        view.getContext().startActivity(in);
                    } else if (checkExt.equalsIgnoreCase("mp4")) {
                        File file = new File(path);
                        // Starting new intent
                        Intent in = new Intent(activity, VideoPlayerActivity.class);
                        in.putExtra("songIndex", path);
                        in.putExtra("songName", filename);

                        // Closing PlayListView
                        view.getContext().startActivity(in);

                    }

                }
            });


        }
    }

    private VideoThumbLoader thumbLoader = new VideoThumbLoader();


}


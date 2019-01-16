package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.AndroidBuildingMusicPlayerActivity;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.VideoDetails;

import java.util.ArrayList;

/**
 * Created by root on 8/9/18.
 */

public class DownloadedMediaList extends BaseAdapter {
    ArrayList<AudioVideo> singletons;
    Activity activity;
    int total_types;
    private LayoutInflater inflater;


    public DownloadedMediaList(Activity activity, ArrayList<AudioVideo> singletons) {
        this.singletons = singletons;
        this.activity = activity;
        total_types = singletons.size();

    }

    public int getCount() {
        return this.singletons.size();
    }

    public Object getItem(int i) {
        return this.singletons.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }


    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (this.inflater == null) {
            this.inflater = (LayoutInflater) this.activity.getLayoutInflater();
            // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.playlist_item, null);
        }

         final TextView imgtitle = (TextView) convertView.findViewById(R.id.songTitle);

        AudioVideo singleton = (AudioVideo) this.singletons.get(i);
        final String filename = singleton.getName();

        final String path = singleton.getFilepath();
        imgtitle.setText(singleton.getName());

        ((LinearLayout) convertView.findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starting new intent
                Intent in = new Intent(activity, AndroidBuildingMusicPlayerActivity.class);
                in.putExtra("songIndex", path);
                in.putExtra("songName", filename);
                // Closing PlayListView
                view.getContext().startActivity(in);

            }
        });




        return convertView;
    }
}

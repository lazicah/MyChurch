package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.disciplesbay.latterhousehq.mychurch.ChannelActivity;
import com.disciplesbay.latterhousehq.mychurch.MainActivity;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.VideoActivity;
import com.disciplesbay.latterhousehq.mychurch.data.VideoDetails;

import java.util.ArrayList;

/**
 * Created by root on 3/18/18.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    ArrayList<VideoDetails> singletons;
    Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    int total_types;
    private LayoutInflater inflater;
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#555555"));


    public HorizontalAdapter(Activity activity, ArrayList<VideoDetails> singletons) {
        this.singletons = singletons;
        this.activity = activity;
        total_types = singletons.size();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_single_row, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (this.imageLoader == null) {
            this.imageLoader = AppController.getInstance().getImageLoader();
        }


        final VideoDetails singleton = (VideoDetails) this.singletons.get(position);


        MyViewHolder holder1 = ((MyViewHolder) holder);
        holder1.networkImageView.setImageUrl(singleton.getURL(), this.imageLoader);
        holder1.title.setText(singleton.getVideoName());

        holder1.cardView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), VideoActivity.class);
                intent.putExtra("videoId", singleton.getVideoId());
                view.getContext().startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return singletons.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, pubDate, url;
        ImageView image;
        NetworkImageView networkImageView;

        private CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.image_view);
            title = (TextView) itemView.findViewById(R.id.title);
           cardView = (CardView) itemView.findViewById(R.id.card_view1);


        }
    }

    public static class defaultViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        ImageView image;

        public defaultViewHolder(View itemView) {
            super(itemView);
            txtType = (TextView) itemView.findViewById(R.id.more1);

            txtType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChannelActivity.class);
                    view.getContext().startActivity(intent);
                }
            });


        }

    }


}
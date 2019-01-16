package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.disciplesbay.latterhousehq.mychurch.Utils.FeedImageView;
import com.disciplesbay.latterhousehq.mychurch.FullImage;
import com.disciplesbay.latterhousehq.mychurch.LoginActivity;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.data.Testimony;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by root on 3/17/18.
 *
 *
 */

public class CaptionedImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private SQLiteHandler db;
    Activity activity;
    private Listener listener;
    private ArrayList<Testimony> testimonyList;
    private int lastPosition = -1;

    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#555555"));

    private SessionManager sessionManager;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public interface Listener {
        void onClick(int position);
    }

    public CaptionedImageAdapter (Activity activity, ArrayList<Testimony> testimonyList){
        this.testimonyList = testimonyList;
        this.activity = activity;

    }

    //tell adapter how many data items are there

    @Override
    public int getItemCount(){
        return testimonyList.size();
    }

    public ArrayList<Testimony> getValues(){
        return testimonyList;
    }

    // activities will use this to register as a listener

    public void setListener(Listener listener){
        this.listener = listener;
    }



    // tell adapter how to create the view holder

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View cv =  LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new TestimonyViewHolder(cv);
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, final int position){

        Date date = null;

        setAnimation(holder.itemView, position);

        TestimonyViewHolder holder1 = (TestimonyViewHolder) holder;
        final Testimony testimony = testimonyList.get(position);

        db = new SQLiteHandler(activity);
        sessionManager = new SessionManager(activity);
        String userId = db.getUserDetails().get("uid");

        String testifier = "by " + testimony.getUsername();
        String you = "by " + "You";


        holder1.rl.setBackgroundColor(testimony.getColor());
        holder1.userImage.setText(testimony.getUsername().substring(0,1));

        if (testimony.getId().equalsIgnoreCase(userId)){

            holder1.userName.setText(you);
        }else {

            holder1.userName.setText(testifier);
        }
        holder1.description.setText(testimony.getDescription());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            date = dateFormat.parse(testimony.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }





        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        holder1.timestamp.setText(timeAgo);
        holder1.likesnumber.setText("(" + testimonyList.get(position).getLikes() + ")");
        if (testimonyList.get(position).getLiked().equals(true)) {
            holder1.like.setImageResource(R.drawable.ic_thumbs_up_red);
            holder1.likesnumber.setTextColor(Color.RED);
        } else {
            holder1.like.setImageResource(R.drawable.ic_thumbs_up);
            holder1.likesnumber.setTextColor(Color.parseColor("#909090"));
        }

        // Feed image
        if (testimony.getImage() != null) {
            holder1.feedImageView.setDefaultImageResId(android.R.drawable.stat_sys_download);
            holder1.feedImageView.setImageUrl(testimony.getImage(), imageLoader);
            holder1.feedImageView.setVisibility(View.VISIBLE);
            holder1.feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder1.feedImageView.setVisibility(View.GONE);

        }



    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        TestimonyViewHolder issueViewHolder = (TestimonyViewHolder) holder;

        if (!payloads.isEmpty()) {
            if (payloads.contains("prelike") && issueViewHolder.likesnumber.getCurrentTextColor() == Color.parseColor("#909090")) {
                ((TestimonyViewHolder) holder).like.setImageResource(R.drawable.ic_thumbs_up_red);
                issueViewHolder.likesnumber.setTextColor(Color.RED);

                issueViewHolder.likesnumber.setText("(" + (Integer.parseInt(testimonyList.get(position).getLikes()) + 1) + ")");
                testimonyList.get(position).setLikes(String.valueOf(Integer.parseInt(testimonyList.get(position).getLikes()) + 1));

            } else if (payloads.contains("preunlike") && issueViewHolder.likesnumber.getCurrentTextColor() == Color.RED) {
                ((TestimonyViewHolder) holder).like.setImageResource(R.drawable.ic_thumbs_up);
                issueViewHolder.likesnumber.setTextColor(Color.parseColor("#909090"));

                issueViewHolder.likesnumber.setText("(" + (Integer.parseInt(testimonyList.get(position).getLikes()) - 1) + ")");
                testimonyList.get(position).setLikes(String.valueOf(Integer.parseInt(testimonyList.get(position).getLikes()) - 1));

            }
        } else super.onBindViewHolder(holder, position, payloads);
    }


    private class TestimonyViewHolder extends RecyclerView.ViewHolder{

        private TextView description;
        private TextView userName, userImage;
        private TextView likesnumber, timestamp;
        private TextView rate;
        private ImageView like;
        private LinearLayout likebox;
        private RelativeLayout rl;
        private FeedImageView feedImageView;
        private CardView cardView;

        private TestimonyViewHolder(final View view){
            super(view);
            rl = (RelativeLayout) view.findViewById(R.id.icon_front);
            likesnumber = (TextView) view.findViewById(R.id.likesnumber);
            like = (ImageView) view.findViewById(R.id.like);
            likebox = (LinearLayout) view.findViewById(R.id.likesbox);
            userName = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.txtStatusMsg);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            userImage= (TextView) view.findViewById(R.id.icon_text);
            feedImageView = (FeedImageView) view.findViewById(R.id.feedImage1);

            feedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startFullImageActivity(getAdapterPosition());

                }
            });

            likebox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sessionManager.isLoggedIn()) {
                        if (testimonyList.get(getAdapterPosition()).getLiked().equals(true)) {
                            testimonyList.get(getAdapterPosition()).setLiked(false);
                            notifyItemChanged(getAdapterPosition(), "preunlike");
                            deleteLike(getAdapterPosition());
                        } else {
                            testimonyList.get(getAdapterPosition()).setLiked(true);
                            notifyItemChanged(getAdapterPosition(), "prelike");
                            updateLike(getAdapterPosition());
                        }

                    }else {

                        Snackbar snackbar = Snackbar.make(view, "Please Login to Continue", Snackbar.LENGTH_LONG);

                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.setAction("LOGIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();


                    }
                }
            });

            cardView = (CardView) view.findViewById(R.id.card_view);







        }
    }

    private void updateLike(final int adapterPosition) {


        db = new SQLiteHandler(activity);
        String apiKey = db.getApikey().get("apikey");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("apiKey", apiKey);
        hashMap.put("for", "testimony");
        hashMap.put("forId", testimonyList.get(adapterPosition).getTestId());
        hashMap.put("reactionId","1");
        Log.d("HashMap", hashMap.toString());
        AndroidNetworking.post("https://www.disciplesbay.com/api/addReaction")
                .addBodyParameter(hashMap)
                .setPriority(Priority.HIGH)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: like" + response);
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equalsIgnoreCase("ok")) {
                                Toast.makeText(activity, "Succesfully Liked", Toast.LENGTH_LONG).show();
                                if (testimonyList.get(adapterPosition).getLiked().equals(false)) {
                                    testimonyList.get(adapterPosition).setLiked(true);
                                    notifyItemChanged(adapterPosition, "prelike");
                                }
                            } else {
                                testimonyList.get(adapterPosition).setLiked(false);
                                notifyItemChanged(adapterPosition, "preunlike");

                                Toast.makeText(activity,
                                        "Error liking", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {

                            Toast.makeText(activity, "couldn't connect", Toast.LENGTH_SHORT).show();
                            if (testimonyList.get(adapterPosition).getLiked().equals(false)) {
                                testimonyList.get(adapterPosition).setLiked(true);
                                notifyItemChanged(adapterPosition, "prelike");
                            } else {
                                testimonyList.get(adapterPosition).setLiked(false);
                                notifyItemChanged(adapterPosition, "preunlike");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });



    }

    private void deleteLike(final int adapterPosition) {


        db = new SQLiteHandler(activity);
        String apiKey = db.getApikey().get("apikey");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("apiKey", apiKey);
        hashMap.put("id", testimonyList.get(adapterPosition).getDeleteId());

        AndroidNetworking.post("https://www.disciplesbay.com/api/deleteReaction")
                .addBodyParameter(hashMap)
                .setPriority(Priority.HIGH)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: like" + response);
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equalsIgnoreCase("ok")) {
                                Toast.makeText(activity, "Successfully Unliked", Toast.LENGTH_LONG).show();
                                if (testimonyList.get(adapterPosition).getLiked().equals(true)) {
                                    testimonyList.get(adapterPosition).setLiked(false);
                                    notifyItemChanged(adapterPosition, "preunlike");
                                }
                            } else {
                                testimonyList.get(adapterPosition).setLiked(true);
                                notifyItemChanged(adapterPosition, "prelike");

                                Toast.makeText(activity,
                                        "Error unLiking", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        try {

                            Toast.makeText(activity, "couldn't connect", Toast.LENGTH_SHORT).show();
                            if (testimonyList.get(adapterPosition).getLiked().equals(true)) {
                                testimonyList.get(adapterPosition).setLiked(false);
                                notifyItemChanged(adapterPosition, "preunlike");
                            } else {
                                testimonyList.get(adapterPosition).setLiked(true);
                                notifyItemChanged(adapterPosition, "prelike");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void startFullImageActivity(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("myData", testimonyList.get(position).getImage());
        bundle.putString("tag", testimonyList.get(position).getUsername());
        bundle.putString("date", testimonyList.get(position).getDate());
        bundle.putString("description", testimonyList.get(position).getDescription());
        Intent in = new Intent(activity, FullImage.class);
        in.putExtras(bundle);
        activity.startActivity(in);
    }



    private void setAnimation(View view, int position){
        if (position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder){
        ((TestimonyViewHolder)holder).itemView.clearAnimation();
    }







}

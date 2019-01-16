package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.data.Comments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 7/14/18.
 */

public class CommentAdapter extends BaseAdapter {
    Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LayoutInflater inflater;
    ArrayList<Comments> singletons;

    public CommentAdapter(Activity activity, ArrayList<Comments> singletons) {
        this.activity = activity;
        this.singletons = singletons;
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
            convertView = this.inflater.inflate(R.layout.comment_item, null);
        }
        if (this.imageLoader == null) {
            this.imageLoader = AppController.getInstance().getImageLoader();
        }
        final TextView userIcon = (TextView) convertView.findViewById(R.id.icon_text);
        final TextView username = (TextView) convertView.findViewById(R.id.name);
        final TextView time = (TextView) convertView.findViewById(R.id.timestamp);
        final  TextView comment=(TextView)convertView.findViewById(R.id.comment);
        final RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.icon_front);
        CircleImageView cv = (CircleImageView) convertView.findViewById(R.id.icon_profile);

        Comments singleton = (Comments) this.singletons.get(i);
        userIcon.setText(singleton.getUsername().substring(0,1));
        username.setText(singleton.getUsername());
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            date = dateFormat.parse(singleton.getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }





        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        time.setText(timeAgo);
        comment.setText(singleton.getComment());



        return convertView;
    }
}

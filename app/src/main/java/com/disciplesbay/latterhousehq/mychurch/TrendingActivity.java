package com.disciplesbay.latterhousehq.mychurch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrendingActivity extends AppCompatActivity {

    ImageView imageView;
    TextView Trend, times;

    private ColorDrawable colorDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        Trend = (TextView) findViewById(R.id.topic);
        times= (TextView) findViewById(R.id.time_of_post);
        imageView = (ImageView) findViewById(R.id.topic_image);

        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));

        Intent intent = getIntent();
        String topic = intent.getExtras().get("topic").toString();
        String time = intent.getExtras().get("time").toString();
        String img = intent.getExtras().get("imgUrl").toString();
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }





        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        Trend.setText(topic);
        times.setText(timeAgo);
        Glide.with(this).load(img).fitCenter().placeholder(colorDrawable).into(imageView);

    }
}

package com.disciplesbay.latterhousehq.mychurch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rey.material.widget.Button;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventDetialsActivity extends AppCompatActivity {

    private TextView Month, Day, EventName,EventLoc, Loc, Details, Time;
    private ImageView eventImg, i, g;
    private Calendar calendar;
    private ColorDrawable colorDrawable;
    private int inter = 0;
    private int going = 0;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        calendar = Calendar.getInstance();
        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));

        Month = (TextView) findViewById(R.id.month);
        Day = (TextView) findViewById(R.id.startDay);
        EventName = (TextView) findViewById(R.id.eventName);
        //EventLoc = (TextView)findViewById(R.id.eventLoc);
        Loc = (TextView) findViewById(R.id.loc);
        Details = (TextView) findViewById(R.id.details);
        Time = (TextView)findViewById(R.id.time);
        eventImg = (ImageView) findViewById(R.id.event_image);
        i = (ImageView) findViewById(R.id.interested);
        g = (ImageView) findViewById(R.id.going);


        ShareButton button = (ShareButton) findViewById(R.id.shareEvent);





        final Intent intent = getIntent();
        String event = intent.getStringExtra("event");
        String time = intent.getStringExtra("time");
        String imgUrl = intent.getStringExtra("imgUrl");
        String details = intent.getStringExtra("details");
        String loc = intent.getStringExtra("loc");

        Date mydate = getValidDate(time);

        // Converting Date to Calendar.
        calendar.setTime(mydate);
        // 0 -> January, 11- December
        int month = calendar.get(Calendar.MONTH);
        String monthName = getMonthName(month);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        DateFormat format = new SimpleDateFormat("EEEE");
        String dayOfWeek = format.format(mydate);



        Month.setText(monthName);
        Day.setText(String.valueOf(day));
        EventName.setText(event);
        if (loc != null){
            //EventLoc.setText(loc);
        }
        if (loc != null){
            Loc.setText(loc);
        }

        Details.setText(details);
        Time.setText(dayOfWeek + " " + String.valueOf(day) + " " + monthName + " " + year);

        if (!imgUrl.isEmpty()){
            Glide.with(this).load(imgUrl).into(eventImg);

        }else {
            Glide.with(this).load(R.drawable.log).into(eventImg);
        }


        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inter == 0){
                    inter = 1;
                    i.setImageResource(R.drawable.ic_star);
                }else {
                    inter = 0;
                    i.setImageResource(R.drawable.ic_star_e);
                }
            }
        });

        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (going == 0){
                    going = 1;
                    g.setImageResource(R.drawable.thumbs_likes_layer);
                }else {
                    going = 0;
                    g.setImageResource(R.drawable.ic_thumbs_up);
                }
            }
        });



    }

    private static Date getValidDate(String date) {

        Date mydate = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*
             * By default setLenient() is true. We should make it false for
			 * strict date validations.
			 *
			 * If setLenient() is true - It accepts all dates. If setLenient()
			 * is false - It accepts only valid dates.
			 */
        dateFormat.setLenient(false);
        try {
            mydate = dateFormat.parse(date);
        } catch (ParseException e) {
            mydate = null;
        }

        return mydate;
    }

    private static String getMonthName(int month) {

        String monthName = null;
        switch (month) {
            case 0:
                monthName = "JAN";
                break;
            case 1:
                monthName = "FEB";
                break;
            case 2:
                monthName = "MAR";
                break;
            case 3:
                monthName = "APR";
                break;
            case 4:
                monthName = "MAY";
                break;
            case 5:
                monthName = "JUN";
                break;
            case 6:
                monthName = "JUL";
                break;
            case 7:
                monthName = "AUG";
                break;
            case 8:
                monthName = "SEP";
                break;
            case 9:
                monthName = "OCT";
                break;
            case 10:
                monthName = "NOV";
                break;
            case 11:
                monthName = "DEC";
                break;
        }

        return monthName;
    }



}

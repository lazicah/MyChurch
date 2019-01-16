package com.disciplesbay.latterhousehq.mychurch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class FullImage extends AppCompatActivity {

    int click = 0;
    private String imagepath;
    private ImageViewTouch imageView;
    private Toolbar toolbar;
    private String tag;
    private TextView headline;
    private String headlinetext;
    private String date, desc;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        initToolbar();
        headline = (TextView) findViewById(R.id.headline);


        Bundle bundle = getIntent().getExtras();
        imagepath = bundle.getString("myData");
        desc = bundle.getString("description");
        tag = bundle.getString("tag");
        headlinetext = bundle.getString("date");
        description = (TextView) findViewById(R.id.testimony_description);
        imageView = (ImageViewTouch) findViewById(R.id.image);

        description.setText(desc);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;

        try {
            date = dateFormat.parse(headlinetext);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        imageView.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {

                if (click == 0) {
                    click = 1;
                    description.setVisibility(View.INVISIBLE);
                } else {
                    click = 0;
                    description.setVisibility(View.VISIBLE);
                }


            }

        });

        getSupportActionBar().setTitle(tag);
        showimage(imagepath);

        if (headlinetext != null) {
            if (!headlinetext.isEmpty()) {
                this.headline.setVisibility(View.VISIBLE);
                this.headline.setText(timeAgo);
            }
        }

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showimage(String i) {
        Glide.with(FullImage.this).load(i).diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().into(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

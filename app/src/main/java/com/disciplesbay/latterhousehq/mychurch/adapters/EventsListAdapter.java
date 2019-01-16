package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import com.disciplesbay.latterhousehq.mychurch.EventDetialsActivity;
import com.disciplesbay.latterhousehq.mychurch.Utils.BlurImage;
import com.disciplesbay.latterhousehq.mychurch.Utils.FeedImageView;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.data.EventsData;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



/**
 * Created by root on 5/5/18.
 */

public class EventsListAdapter extends BaseAdapter {
    private final Context mContext;
    private final SparseBooleanArray mCollapsedStatus;
    List<EventsData> eventsData;
    EventsData events;
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;
    private int BLUR_PRECENTAGE = 80;

    Calendar calendar = Calendar.getInstance();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public EventsListAdapter(Context context, List<EventsData> eventsData) {
        this.mContext = context;
        this.mCollapsedStatus = new SparseBooleanArray();
        this.eventsData = eventsData;

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

    @Override
    public int getCount() {
        return eventsData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        EventsData events = (EventsData) this.eventsData.get(position);


        String date = events.getExpires();
        Date mydate = getValidDate(date);
        // Creating Calendar class instance.

        // Converting Date to Calendar.
        calendar.setTime(mydate);
        // 0 -> January, 11- December
        int month = calendar.get(Calendar.MONTH);
        String monthName = getMonthName(month);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        DateFormat format = new SimpleDateFormat("EEEE");
        String dayOfWeek = format.format(mydate);




        if (convertView == null) {
            if (getItemViewType(position) == EventsData.EVENT) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.newevent_item, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.eventNames);
                TextView textView2 = (TextView) convertView.findViewById(R.id.eventLoc);
                TextView textView6 = (TextView) convertView.findViewById(R.id.date_of_event);
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.event_image);
                LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.eventsLl);
                RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.blur);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDetails(view,position);
                    }
                });
                textView.setText(events.getEventName());
                textView2.setText(events.getLocation());
                textView6.setText(dayOfWeek + " " + String.valueOf(day) + " " + monthName + " " + year);

                //Configure target for
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(BlurImage.fastblur(bitmap, 1f, BLUR_PRECENTAGE));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        imageView.setImageResource(R.mipmap.ic_launcher);

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                imageView.setTag(target);
                Picasso.with(mContext)
                        .load(events.getImage())
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(target);






            } else if (getItemViewType(position) == EventsData.ANNOUNCE) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.newevent_item, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.eventNames);
                TextView textView2 = (TextView) convertView.findViewById(R.id.eventLoc);
                TextView textView6 = (TextView) convertView.findViewById(R.id.date_of_event);
                final ImageView imageView2 = (ImageView) convertView.findViewById(R.id.event_image);
                LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.eventsLl);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            showDetails(view,position);
                    }
                });
                textView.setText(events.getEventName());
                textView2.setText(events.getLocation());
                textView6.setText(dayOfWeek + " " + String.valueOf(day) + " " + monthName + " " + year);

//Configure target for
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView2.setImageBitmap(BlurImage.fastblur(bitmap, 1f, BLUR_PRECENTAGE));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        imageView2.setImageResource(R.mipmap.ic_launcher);

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                if (events.getImage().isEmpty()){
                    imageView2.setTag(target);
                    Picasso.with(mContext)
                            .load(R.drawable.log)
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(target);
                }else {
                    imageView2.setTag(target);
                    Picasso.with(mContext)
                            .load(events.getImage())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(target);
                }








            }
        }


        return convertView;

    }

    @Override
    public int getItemViewType(int position) {
        switch (eventsData.get(position).type) {
            case 1:
                return EventsData.EVENT;
            case 0:
                return EventsData.ANNOUNCE;
            default:
                return -1;
        }
    }

    private void showDialog(int position){

        EventsData eventsDatas = (EventsData) this.eventsData.get(position);

        String date = eventsDatas.getExpires();
        Date mydate = getValidDate(date);
        // Creating Calendar class instance.

        // Converting Date to Calendar.
        calendar.setTime(mydate);
        // 0 -> January, 11- December
        int month = calendar.get(Calendar.MONTH);
        String monthName = getMonthName(month);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_events, null);
        TextView titles = (TextView) confirmDialog.findViewById(R.id.announceName);

        TextView descriptions = (TextView) confirmDialog.findViewById(R.id.announcebody);
        TextView eventDays = (TextView) confirmDialog.findViewById(R.id.announce_day);
        TextView eventYears = (TextView) confirmDialog.findViewById(R.id.announceYear);
        TextView eventMonths = (TextView) confirmDialog.findViewById(R.id.announceMonth);
        FeedImageView feedImageView = (FeedImageView) confirmDialog.findViewById(R.id.feedImage1);
        Button dismis = (Button) confirmDialog.findViewById(R.id.dimissDialog);

        titles.setText(eventsDatas.getEventName());
        descriptions.setText(eventsDatas.getEventDesrciption());
        eventDays.setText(String.valueOf(day));
        eventMonths.setText(monthName);
        eventYears.setText(String.valueOf(year));



        // Feed image
        if (eventsDatas.getImage() != null) {
            feedImageView.setImageUrl(eventsDatas.getImage(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }





        //Creating an alertdialog builder
        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //Displaying the alert dialog
        alertDialog.show();

        dismis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
    }



    private void showDetails(View view, int position){

        EventsData eventsDatas = (EventsData) this.eventsData.get(position);

        String date = eventsDatas.getExpires();
        String details = eventsDatas.getEventDesrciption();
        String event = eventsDatas.getEventName();
        String imgUrl = eventsDatas.getImage();
        String loc = eventsDatas.getLocation();

        Intent intent = new Intent(mContext, EventDetialsActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("time",date);
        intent.putExtra("imgUrl",imgUrl);
        intent.putExtra("details", details);
        intent.putExtra("loc", loc);
        view.getContext().startActivity(intent);
    }






}

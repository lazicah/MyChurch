package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.disciplesbay.latterhousehq.mychurch.EventDetialsActivity;
import com.disciplesbay.latterhousehq.mychurch.EventsActivity;
import com.disciplesbay.latterhousehq.mychurch.MainActivity;
import com.disciplesbay.latterhousehq.mychurch.TrendingActivity;
import com.disciplesbay.latterhousehq.mychurch.Utils.FeedImageView;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.EventsData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 5/14/18.
 */

public class AnnounceAdapter extends RecyclerView.Adapter<AnnounceAdapter.MainViewholder> {


    LayoutInflater inflater;
    Activity activity;
    int total_types;
    Calendar calendar = Calendar.getInstance();
    private List<EventsData> eventsData;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#555555"));


    public AnnounceAdapter(Activity activity, List<EventsData> eventsData) {
        this.activity = activity;
        this.eventsData = eventsData;
        total_types = eventsData.size();

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
    @NonNull
    public MainViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;


        switch (viewType){
            case EventsData.ANNOUNCE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_eventitem_card, parent, false);
                return new EventsViewHolder(view);
            case EventsData.EVENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_eventitem_card, parent, false);
                return new EventsViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MainViewholder viewholder, int position) {


        EventsData eventsDatas = (EventsData) this.eventsData.get(position);


        if (eventsDatas != null) {

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




            switch (viewholder.getItemViewType()){
                case EventsData.ANNOUNCE:
                    EventsViewHolder holder = (EventsViewHolder) viewholder;
                    holder.title.setText(eventsDatas.getEventName());
                    holder.eventDay.setText(String.valueOf(day));
                    holder.eventMonth.setText(monthName);
                    holder.eventYear.setText(String.valueOf(year));


                    if (!eventsDatas.getImage().isEmpty()){
                        Glide.with(activity)
                                .load(eventsDatas.getImage())
                                .fitCenter()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(holder.image);
                    }else if (eventsDatas.getImage().isEmpty()) {
                        Glide.with(activity)
                                .load(R.drawable.log)
                                .fitCenter()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(holder.image);
                    }
                    break;
                case EventsData.EVENT:
                    EventsViewHolder holder1 = (EventsViewHolder) viewholder;
                    holder1.title.setText(eventsDatas.getEventName());
                    holder1.eventDay.setText(String.valueOf(day));
                    holder1.eventMonth.setText(monthName);
                    holder1.eventYear.setText(String.valueOf(year));
                    if (!eventsDatas.getImage().isEmpty()){
                      Glide.with(activity)
                              .load(eventsDatas.getImage())
                              .fitCenter()
                              .dontAnimate()
                              .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                              .into(holder1.image);
                    }else if (eventsDatas.getImage().isEmpty()) {
                      Glide.with(activity)
                              .load(R.drawable.log)
                              .fitCenter()
                              .dontAnimate()
                              .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                              .into(holder1.image);
                    }
                    break;


            }


        }


    }

    @Override
    public int getItemCount() {
        return eventsData.size();
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

    public static class MainViewholder extends RecyclerView.ViewHolder {
        public MainViewholder(View view) {
            super(view);
        }
    }

    public class EventsViewHolder extends MainViewholder {
        TextView title, description, eventDay, eventYear, eventMonth;
        ImageView image;
        LinearLayout lL;



        public EventsViewHolder(View itemView) {
            super(itemView);
            this.lL = (LinearLayout) itemView.findViewById(R.id.lr);
            this.title = (TextView) itemView.findViewById(R.id.eventName);
            this.image = (ImageView) itemView.findViewById(R.id.event_image);

            this.eventDay = (TextView) itemView.findViewById(R.id.event_day);
            this.eventYear = (TextView) itemView.findViewById(R.id.eventYear);
            this.eventMonth = (TextView) itemView.findViewById(R.id.eventMonth);


            lL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDetails(view, getAdapterPosition());
                }
            });



        }
    }

    public class AnnounceViewHolder extends MainViewholder {
        TextView titles, descriptions, eventDays, eventYears, eventMonths;
        LinearLayout lL;




        public AnnounceViewHolder(View itemView) {
            super(itemView);
            this.lL = (LinearLayout) itemView.findViewById(R.id.lr);
            this.titles = (TextView) itemView.findViewById(R.id.announceName);

            this.eventDays = (TextView) itemView.findViewById(R.id.announce_day);
            this.eventYears = (TextView) itemView.findViewById(R.id.announceYear);
            this.eventMonths = (TextView) itemView.findViewById(R.id.announceMonth);


            lL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(getAdapterPosition());
                }
            });

        }
    }

    public class DefaultViewHolder extends MainViewholder {
        TextView txtType;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            txtType = (TextView) itemView.findViewById(R.id.more1);

            txtType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), EventsActivity.class);
                    view.getContext().startActivity(intent);
                }
            });


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

        LayoutInflater li = LayoutInflater.from(activity);
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
            Log.d("IMAGE URL GOTONononoo", eventsDatas.getImage());

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
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);

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

        Intent intent = new Intent(activity, EventDetialsActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("time",date);
        intent.putExtra("imgUrl",imgUrl);
        intent.putExtra("details", details);
        intent.putExtra("loc", loc);
        view.getContext().startActivity(intent);
    }


}

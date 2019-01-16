package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.LocationData;

import java.util.ArrayList;

/**
 * Created by root on 5/19/18.
 */

public class LocationAdapter extends BaseAdapter {
    ArrayList<LocationData> locationDataArrayList;
    LocationData locationData1;
    Activity activity;
    private LayoutInflater inflater;


    public LocationAdapter(ArrayList<LocationData> locationData, Activity activity) {
        this.locationDataArrayList = locationData;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return locationDataArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return locationDataArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (this.inflater == null) {
            this.inflater = (LayoutInflater) this.activity.getLayoutInflater();
            // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.branches_item, null);
        }

        locationData1 = (LocationData) this.locationDataArrayList.get(i);

        TextView textView = (TextView) convertView.findViewById(R.id.location);
        textView.setText(locationData1.getLocation());

        TextView map = (TextView) convertView.findViewById(R.id.view_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse(locationData1.getGmmIntentUri());
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                view.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

}









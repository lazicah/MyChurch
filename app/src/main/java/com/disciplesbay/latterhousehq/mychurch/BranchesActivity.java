package com.disciplesbay.latterhousehq.mychurch;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.disciplesbay.latterhousehq.mychurch.adapters.LocationAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.LocationData;

import java.util.ArrayList;

public class BranchesActivity extends AppCompatActivity {

    private ArrayList<LocationData> locationDataArrayList;
    ListView listView;
    LocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.branchesList);
        locationAdapter = new LocationAdapter(getLocationDataArrayList(), BranchesActivity.this);
        listView.setAdapter(locationAdapter);
    }

    public ArrayList<LocationData> getLocationDataArrayList(){
        ArrayList<LocationData> locationData = new ArrayList<>();
        locationData.add(new LocationData("Address1", "google.streetview:cbll=46.414382, 10.013988"));
        locationData.add(new LocationData("Address2", "google.streetview:cbll=46.414382, 10.013988"));
        locationData.add(new LocationData("Address3", "google.streetview:cbll=46.414382, 10.013988"));
        locationData.add(new LocationData("Address4", "google.streetview:cbll=46.414382, 10.013988"));
        return locationData;
    }
}

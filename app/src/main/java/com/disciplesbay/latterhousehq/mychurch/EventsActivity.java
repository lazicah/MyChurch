package com.disciplesbay.latterhousehq.mychurch;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.adapters.EventsListAdapter;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.app.ConnectivityReceiver;
import com.disciplesbay.latterhousehq.mychurch.data.EventsData;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    List<EventsData> eventsData;
    ListView listView;
    EventsListAdapter eventsListAdapter;
    Snackbar snackbar;
    private SQLiteHandler db;
    private static final String TAG = EventsActivity.class.getSimpleName();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Events");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new SQLiteHandler(getApplicationContext());
        eventsData = new ArrayList<>();

        //EventsData eventsData1 = new EventsData("http://photocdn.sohu.com/tvmobilemvms/20150907/144159406950245847.jpg","My wedding","gonna be interesting", 1, "2018-12-24 02:14:50",0);

        //eventsData.add(eventsData1);

        listView = (ListView) findViewById(R.id.eventList);
        loadEvents();
        if (!db.getAllEvents().isEmpty()){
            eventsListAdapter = new EventsListAdapter(EventsActivity.this, db.getAllEvents() );
            listView.setAdapter(eventsListAdapter);
        }













    }



    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
            snackbar.dismiss();


        } else {
            message = "Not connected to internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();


        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);


    }

    private void loadEvents() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/events?apiKey=" + ay;
        RequestQueue requestQueue ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Event Response: " + response.toString());

                progressDialog.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);
                        int id = jo.getInt("id");
                        //int type = jo.getInt("type");
                        String title = jo.getString("title");
                        String body = jo.getString("body");
                        String expires = jo.getString("expires");
                        String url = jo.getString("thumbnail");
                        String loc= jo.getString("location");
                        if (loc.isEmpty()){
                            EventsData eventsData = new EventsData(url,"none",title, body, id, expires, 0);
                            db.createEvents(eventsData);
                        }
                        EventsData eventsData = new EventsData(url,loc,title, body, id, expires, 0);
                        db.createEvents(eventsData);


                    }

                    eventsListAdapter = new EventsListAdapter(EventsActivity.this, db.getAllEvents() );
                    listView.setAdapter(eventsListAdapter);


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error getting events", Toast.LENGTH_LONG).show();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new
                        HurlStack();
            }
            requestQueue = Volley.newRequestQueue(this, stack);
        } else {
            requestQueue = Volley.newRequestQueue(this);
        }

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    public String deleteExpiredEvents(){
        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(c);
        return date;

    }




}


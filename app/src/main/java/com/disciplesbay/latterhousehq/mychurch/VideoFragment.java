package com.disciplesbay.latterhousehq.mychurch;


import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.disciplesbay.latterhousehq.mychurch.adapters.SermonsAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    ArrayList<AudioVideo> audioVideoList = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar;
    boolean isPaid = false;
    private SQLiteHandler db;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new SQLiteHandler(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.video_messages);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        progressBar = (ProgressBar) view.findViewById(R.id.sermonLoader);

        loadVideo();

        return view;


    }


    private void loadVideo() {


        progressBar.setVisibility(View.VISIBLE);

        final String type = "video";


        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/media?" + "apiKey=" + ay + "&type=" + type;
        RequestQueue requestQueue ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);
                        double price = jo.getDouble("sellingPrice");
                        int id = jo.getInt("id");
                        String url = jo.getString("file");
                        String thumb = jo.getString("thumbnail");
                        String name = jo.getString("title");
                        String description = jo.getString("description");
                        JSONObject bu = jo.getJSONObject("date");
                        String time = bu.getString("normal");

                        String payStatus = jo.getString("userPaymentStatus");
                        if (payStatus.equalsIgnoreCase("paid")) {
                            isPaid = true;
                        } else {
                            isPaid = false;
                        }

                        audioVideoList.add(new AudioVideo(id, url, name, time, description,thumb, price, isPaid, type));

                        SermonsAdapter sermomsCategoryAdapter = new SermonsAdapter(audioVideoList, getActivity());
                        recyclerView.setAdapter(sermomsCategoryAdapter);

                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
            requestQueue = Volley.newRequestQueue(getContext(), stack);
        } else {
            requestQueue = Volley.newRequestQueue(getContext());
        }

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

}

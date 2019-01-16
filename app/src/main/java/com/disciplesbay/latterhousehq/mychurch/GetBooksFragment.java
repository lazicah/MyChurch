package com.disciplesbay.latterhousehq.mychurch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.disciplesbay.latterhousehq.mychurch.adapters.BuyBookAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.Books;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetBooksFragment extends Fragment {

    private SQLiteHandler db;
    ArrayList<Books> audioVideoList = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar;
    boolean isPaid;


    public GetBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_books, container, false);

        db = new SQLiteHandler(getContext());

        progressBar = (ProgressBar) view.findViewById(R.id.top_paidLoader);

        recyclerView = (RecyclerView) view.findViewById(R.id.top_paid_RECYCLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadVideo();


        return view;
    }

    private void loadVideo() {
        final String type = "audio";

        progressBar.setVisibility(View.VISIBLE);


        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/media?apiKey=" + ay + "&type=" + type;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                        String url = jo.getString("fileUrl");
                        String name = jo.getString("title");
                        String description = jo.getString("description");
                        String time = jo.getString("date");
                        String payStatus = jo.getString("userPaymentStatus");

                        if (payStatus.equalsIgnoreCase("paid")){
                            isPaid = true;
                        }else {
                            isPaid = false;
                        }

                        audioVideoList.add(new Books(name,"https://document.desiringgod.org/god-is-the-gospel-en.pdf",time,description,price,isPaid,id));

                        BuyBookAdapter sermomsCategoryAdapter = new BuyBookAdapter(audioVideoList,getActivity());
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

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

}

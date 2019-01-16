package com.disciplesbay.latterhousehq.mychurch;


import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.disciplesbay.latterhousehq.mychurch.adapters.CaptionedImageAdapter;
import com.disciplesbay.latterhousehq.mychurch.adapters.CommentAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.Comments;
import com.disciplesbay.latterhousehq.mychurch.data.Testimony;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    SQLiteHandler db;
    ArrayList<Comments> testimonyArrayList = new ArrayList<>();
    ListView lv;


    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new SQLiteHandler(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        lv = (ListView) view.findViewById(R.id.commentsList);
        loadUrlData();

        ViewCompat.setNestedScrollingEnabled(lv,true);

        return  view;
    }

    private void loadUrlData() {



        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/testimonies?apiKey=" + ay ;


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Testimony Activity", "Send Testimony response " + response.toString());



                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);

                        String name = jo.getString("senderName");
                        String id = jo.getString("senderId");

                        String description = jo.getString("text");
                        String date = jo.getString("dateAdded");
                        int color = getRandomMaterialColor("400");


                        JSONObject userRct = jo.getJSONObject("userReaction");
                        int testId = jo.getInt("id");
                        String reactionId = userRct.getString("reactionId");
                        String deleteId = userRct.getString("id");




                        JSONArray reactions = jo.getJSONArray("reactions");
                        JSONObject ro = reactions.getJSONObject(0);
                        String likess = ro.getString("count");

                        String url = jo.getString("imageUrl");

                        testimonyArrayList.add(new Comments(testId,name,date,description,color));








                    }
                    CommentAdapter adapter = new CommentAdapter(getActivity(), testimonyArrayList);
                    lv.setAdapter(adapter);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

}

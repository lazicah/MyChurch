package com.disciplesbay.latterhousehq.mychurch;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.adapters.DownloadedMediaAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoDownloadsFragment extends Fragment {


    private ArrayList<AudioVideo> List = new ArrayList<>();
    private VideoManager videoManager;


    RecyclerView recyclerView;
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private ProgressDialog progressDialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_downloads, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.myBook_RECYCLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new myLoader().execute(getActivity());



        videoManager = new VideoManager();
        progressDialog = new ProgressDialog(getContext());




        return view;
    }

    private class myLoader extends AsyncTask<Activity, Void, ArrayList>{


        @Override
        protected void onPreExecute() {


        }

        @Override
        protected ArrayList doInBackground(Activity... activities) {
            List = videoManager.getPlayList(activities[0]);
            return List;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            progressDialog.dismiss();

            DownloadedMediaAdapter downloadedMediaAdapter = new DownloadedMediaAdapter(getActivity(),arrayList);
            recyclerView.setAdapter(downloadedMediaAdapter);
        }
    }





}

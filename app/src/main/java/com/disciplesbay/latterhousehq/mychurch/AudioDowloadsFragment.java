package com.disciplesbay.latterhousehq.mychurch;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.adapters.BookAdapter;
import com.disciplesbay.latterhousehq.mychurch.adapters.DownloadedMediaAdapter;
import com.disciplesbay.latterhousehq.mychurch.adapters.DownloadedMediaList;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.Books;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioDowloadsFragment extends Fragment {


    private ArrayList<AudioVideo> audioVideoList;
    private ProgressDialog progressDialog;
    private SongsManager songsManager;


    RecyclerView recyclerView;
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_dowloads, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.myBook_RECYCLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        audioVideoList = new ArrayList<>();






        songsManager = new SongsManager();


        return view;
    }

    private class myLoaders extends AsyncTask<Activity, Void, ArrayList> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList doInBackground(Activity... activities) {
            audioVideoList = songsManager.getPlayLists(activities[0]);
            return audioVideoList;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {

            DownloadedMediaList downloadedMediaList = new DownloadedMediaList(getActivity(), arrayList);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            new myLoaders().execute(getActivity());
        }
    }
}

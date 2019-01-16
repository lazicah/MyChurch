package com.disciplesbay.latterhousehq.mychurch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.adapters.BookAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.Books;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyBooksFragment extends Fragment {

    ArrayList<Books> audioVideoList = new ArrayList<>();


    RecyclerView recyclerView;
    TextView textView;


    public MyBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);

        textView = (TextView) view.findViewById(R.id.noBook);
        recyclerView = (RecyclerView) view.findViewById(R.id.myBook_RECYCLE);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        pdfManager pdfM = new pdfManager();


        this.audioVideoList = pdfM.getPlayList(getActivity());
        if (this.audioVideoList.isEmpty()){
            textView.setVisibility(View.VISIBLE);

        }


        BookAdapter bookAdapter = new BookAdapter(getActivity(), audioVideoList);
        recyclerView.setAdapter(bookAdapter);

        return view;
    }

}

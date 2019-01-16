package com.disciplesbay.latterhousehq.mychurch.adapters;

/**
 * Created by root on 4/29/18.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.disciplesbay.latterhousehq.mychurch.data.GivingData;
import com.disciplesbay.latterhousehq.mychurch.R;


/**
 * Created by root on 3/17/18.
 */

public class GivingAdapter extends RecyclerView.Adapter<GivingAdapter.ViewHolder> {


    private Listener listener;
    private ArrayList<GivingData> givingData;

    public GivingAdapter(ArrayList<GivingData> givingData) {
        this.givingData = givingData;
    }

    public interface Listener {
        void onClick(int position);
    }




    //tell adapter how many data items are there

    @Override
    public int getItemCount(){
        return givingData.size();
    }

    // activities will use this to register as a listener

    public void setListener(Listener listener){
        this.listener = listener;
    }

    //Define adapter view holder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;

        public ViewHolder (CardView v){
            super(v);
            cardView = v;
        }
    }

    // tell adapter how to create the view holder

    @Override
    public GivingAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.giving_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        CardView cardView = holder.cardView;

        final GivingData giving = givingData.get(position);;





        TextView textView2 = (TextView) cardView.findViewById(R.id.givingText);
        textView2.setText(giving.getGivingName());


        View view = cardView.findViewById(R.id.layout_card_num);
        view.setBackgroundColor(giving.getColor());








        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

}


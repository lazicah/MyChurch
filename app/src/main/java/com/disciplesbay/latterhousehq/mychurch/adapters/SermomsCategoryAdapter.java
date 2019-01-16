package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.SermonCategory;

import java.util.List;


/**
 * Created by root on 3/17/18.
 */

public class SermomsCategoryAdapter extends RecyclerView.Adapter<SermomsCategoryAdapter.ViewHolder> {



    private Listener listener;
    private List<SermonCategory> testimonyList;

    public interface Listener {
        void onClick(int position);
    }

    public SermomsCategoryAdapter (List<SermonCategory> testimonyList){
        this.testimonyList = testimonyList;

    }

    //tell adapter how many data items are there

    @Override
    public int getItemCount(){
        return testimonyList.size();
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
    public SermomsCategoryAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.sermon_card, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        CardView cardView = holder.cardView;

        final SermonCategory sermonCategory = testimonyList.get(position);;

        ImageView imageView = (ImageView) cardView.findViewById(R.id.img);
        imageView.setImageResource(sermonCategory.getImg());

        String col = "#F44336";

        int color = Integer.parseInt(col.replaceFirst("^#",""),16);



        cardView.setCardBackgroundColor(Color.parseColor("#F44336"));


        TextView textView = (TextView) cardView.findViewById(R.id.category);
        textView.setText(sermonCategory.getCategory());


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

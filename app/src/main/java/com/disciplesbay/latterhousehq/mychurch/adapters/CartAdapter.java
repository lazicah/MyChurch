package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.data.CartModel;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;

import java.util.ArrayList;

/**
 * Created by root on 7/6/18.
 */

public class CartAdapter extends BaseAdapter {
    Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LayoutInflater inflater;
    ArrayList<CartModel> singletons;
    SQLiteHandler db = new SQLiteHandler(activity);

    public CartAdapter(Activity activity, ArrayList<CartModel> singletons) {
        this.activity = activity;
        this.singletons = singletons;
    }

    public int getCount() {
        return this.singletons.size();
    }

    public Object getItem(int i) {
        return this.singletons.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public ArrayList<CartModel> getValues(){
        return singletons;
    }

    public View getView(final int p, View convertView, ViewGroup viewGroup) {
        if (this.inflater == null) {
            this.inflater = (LayoutInflater) this.activity.getLayoutInflater();
            // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.cart_item, null);
        }
        if (this.imageLoader == null) {
            this.imageLoader = AppController.getInstance().getImageLoader();
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.product_image);
        final TextView productName = (TextView) convertView.findViewById(R.id.name_product);

        final  TextView productAmount=(TextView)convertView.findViewById(R.id.amount);
        final CartModel singleton = (CartModel) this.singletons.get(p);

        /*((LinearLayout) convertView.findViewById(R.id.asser)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Remover this Item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        db.deleteCartItem(singleton.getId());
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });*/



        imageView.setImageResource(R.drawable.traffic_icon);

        productName.setText(singleton.getProductName());
        productAmount.setText("NGN" + String.valueOf(singleton.getAmount()) + ".00");
        return convertView;
    }


}